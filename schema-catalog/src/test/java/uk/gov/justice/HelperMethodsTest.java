package uk.gov.justice;

import static org.junit.Assert.assertEquals;

import uk.gov.justice.schema.catalog.CatalogLoader;

import org.junit.Test;

public class HelperMethodsTest {

    @Test
    public void testExtractJarName() {
        String input = "jar:file:/Users/joao/projects/MOJ/CJSCommonPlatform/myserviceparent/schema-catalog/target/schema-catalog-1.0-SNAPSHOT.jar!/json/schema/schema_catalog.json";
        assertEquals("/Users/joao/projects/MOJ/CJSCommonPlatform/myserviceparent/schema-catalog/target/schema-catalog-1.0-SNAPSHOT.jar", CatalogLoader.extractJarName(input));

    }


}
