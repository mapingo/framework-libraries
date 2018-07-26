package uk.gov.moj.cpp.jobmanager.it;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import uk.gov.justice.services.common.configuration.GlobalValueProducer;
import uk.gov.justice.services.common.configuration.JndiBasedServiceContextNameProvider;
import uk.gov.justice.services.common.configuration.ValueProducer;
import uk.gov.justice.services.common.converter.JsonObjectToObjectConverter;
import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.core.mapping.NameToMediaTypeConverter;
import uk.gov.justice.services.core.mapping.SchemaIdMappingCache;
import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryHelper;
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;
import uk.gov.justice.services.test.utils.common.envelope.EnvelopeRecordingInterceptor;
import uk.gov.justice.services.test.utils.common.reflection.ReflectionUtils;
import uk.gov.moj.cpp.jobmanager.it.util.LoggerProducer;
import uk.gov.moj.cpp.jobmanager.it.util.OpenEjbConfigurationBuilder;
import uk.gov.moj.cpp.jobmanager.it.util.OpenEjbJobJdbcRepository;
import uk.gov.moj.cpp.jobstore.api.ExecutionService;
import uk.gov.moj.cpp.jobstore.persistence.AnsiJobSqlProvider;
import uk.gov.moj.cpp.jobstore.persistence.InitialContextProducer;
import uk.gov.moj.cpp.jobstore.persistence.JdbcJobStoreDataSourceProvider;
import uk.gov.moj.cpp.jobstore.persistence.Job;
import uk.gov.moj.cpp.jobstore.persistence.JobRepository;
import uk.gov.moj.cpp.jobstore.persistence.JobSqlProvider;
import uk.gov.moj.cpp.jobstore.service.JobService;
import uk.gov.moj.cpp.task.execution.JobScheduler;
import uk.gov.moj.cpp.task.extension.SampleTask;
import uk.gov.moj.cpp.task.extension.TaskRegistry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Application;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ApplicationComposer.class)
public class JobSchedulerIT {

    private static final String LIQUIBASE_JOB_STORE_DB_CHANGELOG_XML = "liquibase/jobstore-db-changelog.xml";

    @Inject
    OpenEjbJobJdbcRepository testJobJdbcRepository;

    @Module
    @Classes(cdi = true, value = {
            JobService.class,
            ExecutionService.class,
            JobRepository.class,
            JdbcJobStoreDataSourceProvider.class,
            JdbcRepositoryHelper.class,
            JobSqlProvider.class,
            OpenEjbJobJdbcRepository.class,
            JobScheduler.class,
            GlobalValueProducer.class,
            TaskRegistry.class,

            DefaultJsonObjectEnvelopeConverter.class,
            DefaultTraceLogger.class,

            EnvelopeRecordingInterceptor.class,

            StringToJsonObjectConverter.class,
            JsonObjectEnvelopeConverter.class,
            ObjectMapper.class,

            JndiBasedServiceContextNameProvider.class,
            ValueProducer.class,
            GlobalValueProducer.class,

            JsonObjectToObjectConverter.class,
            SchemaIdMappingCache.class,
            ObjectToJsonObjectConverter.class,
            UtcClock.class,
            NameToMediaTypeConverter.class,
            SchemaIdMappingCache.class,
            InitialContextProducer.class,
            LoggerProducer.class,
            SampleTask.class

    },
            cdiAlternatives = {AnsiJobSqlProvider.class})

    public WebApp war() {
        return new WebApp()
                .contextRoot("notificationnotify-test")
                .addServlet("ServiceApp", Application.class.getName());
    }

    @Resource(name = "openejb/Resource/jobStore")
    private DataSource dataSource;

    @Inject
    JobService jobService;

    @Inject
    JobScheduler jobScheduler;

    @Before
    public void setup() throws Exception {
        InitialContext initialContext = new InitialContext();
        initialContext.bind("java:/app/JobSchedulerIT/DS.jobstore", dataSource);
        initEventDatabase();
        ReflectionUtils.setField(jobService, "jobCount", "10");
    }

    @Configuration
    public Properties configuration() {
        return OpenEjbConfigurationBuilder.createOpenEjbConfigurationBuilder()
                .addInitialContext()
                .addHttpEjbPort(8080)
                .addH2JobStore()
                .build();
    }

    @Inject
    UserTransaction userTransaction;


    public void initEventDatabase() throws Exception {
        Liquibase eventStoreLiquibase = new Liquibase(LIQUIBASE_JOB_STORE_DB_CHANGELOG_XML,
                new ClassLoaderResourceAccessor(), new JdbcConnection(dataSource.getConnection()));

        eventStoreLiquibase.update("");

        ReflectionUtils.setField(jobService, "jobRepository", testJobJdbcRepository);
    }

    @Test
    public void shouldNotPerformDuplicateJobUpdates() throws Exception {
        userTransaction.begin();
        testJobJdbcRepository.cleanJobTables();
        testJobJdbcRepository.createJobs(50);
        userTransaction.commit();

        userTransaction.begin();
        final CyclicBarrier gate = new CyclicBarrier(4);
        IntStream.range(0, 3)
                .mapToObj(threadNo -> getThreadCurrentImpl(gate))
                 .forEach(t -> t.start());
        gate.await();
        Thread.sleep(3000);
        userTransaction.commit();

        assertThat(testJobJdbcRepository.jobsNotProcessed(), CoreMatchers.is(20));
    }


    private Thread getThreadCurrentImpl(final CyclicBarrier gate) {
        return new Thread(() -> {
            try {
                gate.await();
                userTransaction.begin();

                jobScheduler.fetchUnassignedJobs();
                assertExpectedJobAssigments();

                userTransaction.commit();

            } catch (InterruptedException | NotSupportedException | SystemException | BrokenBarrierException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException | SQLException e) {
                throw new RuntimeException("Failed to complete transaction ");
            }

        });
    }

    private void assertExpectedJobAssigments() throws SQLException {
        final Stream<Job> jobStream = testJobJdbcRepository.getProcessedRecords();
        final Map<UUID, List<Job>> jobByWorkerIdMap = jobStream.collect(Collectors.groupingBy(x -> x.getWorkerId().get()));
        final Set<UUID> jobs = new HashSet();
        final List<UUID> duplicates = new ArrayList<>();

        testJobJdbcRepository.collectDuplicates(jobs, duplicates, jobByWorkerIdMap);

        duplicates.forEach(job -> System.out.println(" Duplicated Job: " + job));
        assertThat(duplicates.size(), is(0));
    }
}
