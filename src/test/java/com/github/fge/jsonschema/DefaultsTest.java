package com.github.fge.jsonschema;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;

public class DefaultsTest {
    private final JsonNode testNode;

    public DefaultsTest() throws IOException {
        testNode = JsonLoader.fromResource("/other/defaults.json");
        System.out.println(testNode);
    }

    @DataProvider
    public Iterator<Object[]> testData() {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode node : testNode) {
            list.add(new Object[] { node.get("input"), node.get("output") });
        }

        return list.iterator();
    }

    @Test(dataProvider = "testData")
    public void defaultsAreExtractedCorrectly(final JsonNode input, final JsonNode expected)
            throws ProcessingException {
        JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(input);
        assertTrue(schema.validInstance(expected));
        JsonNode actual = schema.extractDefault();
        assertEquals(new JsonEq(actual), new JsonEq(expected));
    }

    private static final Equivalence<JsonNode> EQUIVALENCE = JsonNumEquals.getInstance();

    private static class JsonEq {
        private final JsonNode node;

        public JsonEq(JsonNode node) {
            this.node = node;
        }

        @Override
        public boolean equals(Object obj) {
            return EQUIVALENCE.equivalent(this.node, ((JsonEq) obj).node);
        }

        @Override
        public String toString() {
            return node.toString();
        }
    }
}
