
package org.udg.caes.banking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.udg.caes.banking.entity.Account;
import org.udg.caes.banking.entity.Client;
import org.udg.caes.banking.entity.CreditCard;

import org.udg.caes.banking.exceptions.*;
import org.udg.caes.banking.manager.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestClientService {

    @Mock
    EntityManager em;

    @Mock
    AccountService as;

    @InjectMocks
    ClientService clientService;

    @Test
    void testGetClient() throws Exception {
        Client client = new Client("C1");

        when(em.get("C1", Client.class)).thenReturn(client);

        Client result = clientService.getClient("C1");

        assertSame(client, result);
    }

    @Test
    void testGetClientNotFound() throws Exception {
        when(em.get("C1", Client.class))
                .thenThrow(new EntityNotFound());

        assertThrows(ClientNotFound.class,
                () -> clientService.getClient("C1"));
    }

    @Test
    void testDeleteClientWithoutAccounts() throws Exception {
        Client client = new Client("C1");

        when(em.get("C1", Client.class)).thenReturn(client);
        when(em.getClientAccounts(client)).thenReturn(List.of());

        clientService.delete("C1");

        verifyNoInteractions(as);
    }

    @Test
    void testDeleteClientWithAccounts() throws Exception {
        Client client = new Client("C1");
        Account a1 = new Account("A1", 0);
        Account a2 = new Account("A2", 0);

        when(em.get("C1", Client.class)).thenReturn(client);
        when(em.getClientAccounts(client))
                .thenReturn(List.of(a1, a2));

        clientService.delete("C1");

        verify(as).delete(a1);
        verify(as).delete(a2);
    }

    @Test
    void testDeleteClientNotFound() throws Exception {
        when(em.get("C1", Client.class))
                .thenThrow(new EntityNotFound());

        assertThrows(ClientNotFound.class,
                () -> clientService.delete("C1"));

        verifyNoInteractions(as);
    }

    @Test
    void testDeleteClientAccountActive() throws Exception {
        Client client = new Client("C1");
        Account account = new Account("A1", 100);

        when(em.get("C1", Client.class)).thenReturn(client);
        when(em.getClientAccounts(client))
                .thenReturn(List.of(account));

        doThrow(new AccountActive()).when(as).delete(account);

        assertThrows(AccountActive.class,
                () -> clientService.delete("C1"));
    }

    @Test
    void testGetBalanceWithoutAccounts() {
        Client client = new Client("C1");

        when(em.getClientAccounts(client)).thenReturn(List.of());

        long result = clientService.getBalance(client);

        assertEquals(0, result);
    }

    @Test
    void testGetBalanceWithAccounts() {
        Client client = new Client("C1");
        Account a1 = new Account("A1", 100);
        Account a2 = new Account("A2", 200);

        when(em.getClientAccounts(client))
                .thenReturn(List.of(a1, a2));

        long result = clientService.getBalance(client);

        assertEquals(300, result);
    }

    @Test
    void testGetRealBalanceWithoutAccounts() {
        Client client = new Client("C1");

        when(em.getClientAccounts(client)).thenReturn(List.of());

        long result = clientService.getRealBalance(client);

        assertEquals(0, result);
    }

    @Test
    void testGetRealBalanceWithoutCards() {
        Client client = new Client("C1");
        Account account = new Account("A1", 100);

        when(em.getClientAccounts(client))
                .thenReturn(List.of(account));
        when(em.getCreditCards(account)).thenReturn(List.of());

        long result = clientService.getRealBalance(client);

        assertEquals(100, result);
    }

    @Test
    void testGetRealBalanceWithCards() {
        Client client = new Client("C1");

        Account a1 = new Account("A1", 200);
        Account a2 = new Account("A2", 150);

        CreditCard cc1 = new CreditCard("CC1");
        CreditCard cc2 = new CreditCard("CC2");

        cc1.credit(30);
        cc2.credit(20);

        when(em.getClientAccounts(client))
                .thenReturn(List.of(a1, a2));

        when(em.getCreditCards(a1))
                .thenReturn(List.of(cc1, cc2));

        when(em.getCreditCards(a2))
                .thenReturn(List.of());

        long result = clientService.getRealBalance(client);

        assertEquals(300, result);
    }
}
