package uk.gov.justice.api.resource;

import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonObject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import uk.gov.justice.services.adapter.rest.mapping.ActionMapper;
import uk.gov.justice.services.adapter.rest.multipart.FileInputDetailsFactory;
import uk.gov.justice.services.adapter.rest.parameter.ParameterCollectionBuilder;
import uk.gov.justice.services.adapter.rest.parameter.ParameterCollectionBuilderFactory;
import uk.gov.justice.services.adapter.rest.parameter.ParameterType;
import uk.gov.justice.services.adapter.rest.processor.RestProcessor;
import uk.gov.justice.services.core.annotation.Adapter;
import uk.gov.justice.services.core.annotation.Component;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.messaging.logging.HttpTraceLoggerHelper;
import uk.gov.justice.services.messaging.logging.TraceLogger;

@Adapter(Component.COMMAND_API)
public class DefaultCommandApiCasesCaseIdResource implements CommandApiCasesCaseIdResource {
  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DefaultCommandApiCasesCaseIdResource.class);

  @Inject
  RestProcessor restProcessor;

  @Inject
  @Named("DefaultCommandApiCasesCaseIdResourceActionMapper")
  ActionMapper actionMapper;

  @Inject
  InterceptorChainProcessor interceptorChainProcessor;

  @Context
  HttpHeaders headers;

  @Inject
  FileInputDetailsFactory fileInputDetailsFactory;

  @Inject
  ParameterCollectionBuilderFactory validParameterCollectionBuilderFactory;

  @Inject
  TraceLogger traceLogger;

  @Inject
  HttpTraceLoggerHelper httpTraceLoggerHelper;

  @Override
  public Response postProgressionCommandAddCaseToCrownCourtCasesByCaseId(final String caseId, final JsonObject entity) {
    final ParameterCollectionBuilder validParameterCollectionBuilder = validParameterCollectionBuilderFactory.create();
    traceLogger.trace(LOGGER, () -> String.format("Received REST request with headers: %s", httpTraceLoggerHelper.toHttpHeaderTrace(headers)));
    validParameterCollectionBuilder.putRequired("caseId", caseId, ParameterType.STRING);
    return restProcessor.process("AcceptedStatusNoEntityResponseStrategy", interceptorChainProcessor::process, actionMapper.actionOf("postProgressionCommandAddCaseToCrownCourtCasesByCaseId", "POST", headers), Optional.of(entity), headers, validParameterCollectionBuilder.parameters());
  }

  @Override
  public Response postProgressionCommandAddSentenceHearingCasesByCaseId(final String caseId, final JsonObject entity) {
    final ParameterCollectionBuilder validParameterCollectionBuilder = validParameterCollectionBuilderFactory.create();
    traceLogger.trace(LOGGER, () -> String.format("Received REST request with headers: %s", httpTraceLoggerHelper.toHttpHeaderTrace(headers)));
    validParameterCollectionBuilder.putRequired("caseId", caseId, ParameterType.STRING);
    return restProcessor.process("AcceptedStatusNoEntityResponseStrategy", interceptorChainProcessor::process, actionMapper.actionOf("postProgressionCommandAddSentenceHearingCasesByCaseId", "POST", headers), Optional.of(entity), headers, validParameterCollectionBuilder.parameters());
  }

  @Override
  public Response postProgressionCommandCaseAssignedForReviewCasesByCaseId(final String caseId, final JsonObject entity) {
    final ParameterCollectionBuilder validParameterCollectionBuilder = validParameterCollectionBuilderFactory.create();
    traceLogger.trace(LOGGER, () -> String.format("Received REST request with headers: %s", httpTraceLoggerHelper.toHttpHeaderTrace(headers)));
    validParameterCollectionBuilder.putRequired("caseId", caseId, ParameterType.STRING);
    return restProcessor.process("AcceptedStatusNoEntityResponseStrategy", interceptorChainProcessor::process, actionMapper.actionOf("postProgressionCommandCaseAssignedForReviewCasesByCaseId", "POST", headers), Optional.of(entity), headers, validParameterCollectionBuilder.parameters());
  }

  @Override
  public Response postProgressionCommandCaseToBeAssignedCasesByCaseId(final String caseId, final JsonObject entity) {
    final ParameterCollectionBuilder validParameterCollectionBuilder = validParameterCollectionBuilderFactory.create();
    traceLogger.trace(LOGGER, () -> String.format("Received REST request with headers: %s", httpTraceLoggerHelper.toHttpHeaderTrace(headers)));
    validParameterCollectionBuilder.putRequired("caseId", caseId, ParameterType.STRING);
    return restProcessor.process("AcceptedStatusNoEntityResponseStrategy", interceptorChainProcessor::process, actionMapper.actionOf("postProgressionCommandCaseToBeAssignedCasesByCaseId", "POST", headers), Optional.of(entity), headers, validParameterCollectionBuilder.parameters());
  }

  @Override
  public Response postProgressionCommandPrepareForSentenceHearingCasesByCaseId(final String caseId, final JsonObject entity) {
    final ParameterCollectionBuilder validParameterCollectionBuilder = validParameterCollectionBuilderFactory.create();
    traceLogger.trace(LOGGER, () -> String.format("Received REST request with headers: %s", httpTraceLoggerHelper.toHttpHeaderTrace(headers)));
    validParameterCollectionBuilder.putRequired("caseId", caseId, ParameterType.STRING);
    return restProcessor.process("AcceptedStatusNoEntityResponseStrategy", interceptorChainProcessor::process, actionMapper.actionOf("postProgressionCommandPrepareForSentenceHearingCasesByCaseId", "POST", headers), Optional.of(entity), headers, validParameterCollectionBuilder.parameters());
  }

  @Override
  public Response postProgressionCommandSendingCommittalHearingInformationCasesByCaseId(final String caseId, final JsonObject entity) {
    final ParameterCollectionBuilder validParameterCollectionBuilder = validParameterCollectionBuilderFactory.create();
    traceLogger.trace(LOGGER, () -> String.format("Received REST request with headers: %s", httpTraceLoggerHelper.toHttpHeaderTrace(headers)));
    validParameterCollectionBuilder.putRequired("caseId", caseId, ParameterType.STRING);
    return restProcessor.process("AcceptedStatusNoEntityResponseStrategy", interceptorChainProcessor::process, actionMapper.actionOf("postProgressionCommandSendingCommittalHearingInformationCasesByCaseId", "POST", headers), Optional.of(entity), headers, validParameterCollectionBuilder.parameters());
  }

  @Override
  public Response postProgressionCommandSentenceHearingDateCasesByCaseId(final String caseId, final JsonObject entity) {
    final ParameterCollectionBuilder validParameterCollectionBuilder = validParameterCollectionBuilderFactory.create();
    traceLogger.trace(LOGGER, () -> String.format("Received REST request with headers: %s", httpTraceLoggerHelper.toHttpHeaderTrace(headers)));
    validParameterCollectionBuilder.putRequired("caseId", caseId, ParameterType.STRING);
    return restProcessor.process("AcceptedStatusNoEntityResponseStrategy", interceptorChainProcessor::process, actionMapper.actionOf("postProgressionCommandSentenceHearingDateCasesByCaseId", "POST", headers), Optional.of(entity), headers, validParameterCollectionBuilder.parameters());
  }
}
