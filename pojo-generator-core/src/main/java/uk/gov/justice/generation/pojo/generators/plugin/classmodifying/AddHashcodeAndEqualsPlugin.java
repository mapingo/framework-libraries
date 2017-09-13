package uk.gov.justice.generation.pojo.generators.plugin.classmodifying;

import static com.squareup.javapoet.ClassName.get;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.INT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.PUBLIC;

import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.dom.Definition;

import java.util.List;
import java.util.Objects;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Add java {@link Object#equals(Object)} and {@link Object#hashCode()} to the generated class.
 * The hashCode and equals methods use {@link Objects#hashCode(Object)} and
 * {@link Objects#equals(Object, Object)} under the hood.
 *
 * NB: this plugin will not add {@link Object#equals(Object)} nor {@link Object#hashCode()} if
 * the class definition contains no fields
 */
public class AddHashcodeAndEqualsPlugin implements ClassModifyingPlugin {

    @Override
    public TypeSpec.Builder generateWith(
            final TypeSpec.Builder classBuilder,
            final ClassDefinition classDefinition,
            final PluginContext pluginContext) {

        final MethodSpec equalsMethod = generateEquals(classDefinition, pluginContext);
        final MethodSpec hashCodeMethod = generateHashcode(classDefinition);

        if (!classDefinition.getFieldDefinitions().isEmpty()) {
            classBuilder
                    .addMethod(equalsMethod)
                    .addMethod(hashCodeMethod);
        }

        return classBuilder;
    }

    private MethodSpec generateHashcode(final ClassDefinition classDefinition) {

        final List<String> fieldNames = classDefinition.getFieldDefinitions()
                .stream()
                .map(Definition::getFieldName)
                .collect(toList());

        if (classDefinition.allowAdditionalProperties()) {
            fieldNames.add("additionalProperties");
        }

        final String fields = fieldNames
                .stream()
                .collect(joining(", "));



        return methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(INT)
                .addCode("return $L.hash(" + fields + ");", get(Objects.class))
                .build();
    }

    private MethodSpec generateEquals(final ClassDefinition classDefinition, final PluginContext pluginContext) {

        final TypeName className = pluginContext
                .getClassNameFactory()
                .createClassNameFrom(classDefinition);

        final List<String> fieldNames = classDefinition.getFieldDefinitions()
                .stream()
                .map(Definition::getFieldName)
                .collect(toList());

        if (classDefinition.allowAdditionalProperties()) {
            fieldNames.add("additionalProperties");
        }

        final MethodSpec.Builder methodBuilder = methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(get(Object.class), "obj", Modifier.FINAL)
                .returns(BOOLEAN)
                .addStatement("if (this == obj) return true")
                .addStatement("if (obj == null || getClass() != obj.getClass()) return false")
                .addStatement("final $L that = ($L) obj", className, className)
                .addCode("\n")
                .addCode("return ");

        final String equalsStatements = fieldNames
                .stream()
                .map(this::createEqualsStatement)
                .collect(joining(" &&\n"));

        return methodBuilder
                .addCode(equalsStatements)
                .addCode(";\n")
                .build();
    }

    private String createEqualsStatement(final String fieldName) {
        return CodeBlock.builder().add("$L.equals(this.$L, that.$L)", get(Objects.class), fieldName, fieldName).build().toString();
    }
}
