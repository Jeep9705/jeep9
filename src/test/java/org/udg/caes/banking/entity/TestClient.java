package org.udg.caes.banking.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestClient {

    @Test
    void testClient() {
        Client client = new Client("client1");

        assertEquals("client1", client.getId());
    }
}