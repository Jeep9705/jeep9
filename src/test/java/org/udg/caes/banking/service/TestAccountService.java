
package org.udg.caes.banking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.udg.caes.banking.entity.Account;
import org.udg.caes.banking.entity.CreditCard;
import org.udg.caes.banking.exceptions.*;
import org.udg.caes.banking.manager.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestAccountService {

    @Mock
    EntityManager em;

    @Mock
    CreditCardService ccs;

    @InjectMocks
    AccountService accountService;

    @Test
    void testGetAccount() throws Exception {
        Account account = new Account("A1", 100);

        when(em.get("A1", Account.class)).thenReturn(account);

        Account result = accountService.getAccount("A1");

        assertSame(account, result);
    }

    @Test
    void testGetAccountNotFound() throws Exception {
        when(em.get("A1", Account.class))
                .thenThrow(new EntityNotFound());

        assertThrows(AccountNotFound.class,
                () -> accountService.getAccount("A1"));
    }

    @Test
    void testTransfer() throws Exception {
        Account from = new Account("A1", 100);
        Account to = new Account("A2", 50);

        when(em.get("A1", Account.class)).thenReturn(from);
        when(em.get("A2", Account.class)).thenReturn(to);

        accountService.transfer("A1", "A2", 40);

        assertEquals(60, from.getBalance());
        assertEquals(90, to.getBalance());

        verify(em).persist(from);
        verify(em).persist(to);
    }

    @Test
    void testTransferNotEnoughBalance() throws Exception {
        Account from = new Account("A1", 20);
        Account to = new Account("A2", 50);

        when(em.get("A1", Account.class)).thenReturn(from);
        when(em.get("A2", Account.class)).thenReturn(to);

        assertThrows(NotEnoughBalance.class,
                () -> accountService.transfer("A1", "A2", 40));

        assertEquals(20, from.getBalance());
        assertEquals(50, to.getBalance());

        verify(em, never()).persist(any());
    }

    @Test
    void testTransferFromNotFound() throws Exception {
        when(em.get("A1", Account.class))
                .thenThrow(new EntityNotFound());

        assertThrows(AccountNotFound.class,
                () -> accountService.transfer("A1", "A2", 40));

        verify(em, never()).persist(any());
    }

    @Test
    void testTransferToNotFound() throws Exception {
        Account from = new Account("A1", 100);

        when(em.get("A1", Account.class)).thenReturn(from);
        when(em.get("A2", Account.class))
                .thenThrow(new EntityNotFound());

        assertThrows(AccountNotFound.class,
                () -> accountService.transfer("A1", "A2", 40));

        assertEquals(100, from.getBalance());
        verify(em, never()).persist(any());
    }

    @Test
    void testDeleteAccountWithBalance() throws Exception {
        Account account = new Account("A1", 100);

        assertThrows(AccountActive.class,
                () -> accountService.delete(account));

        verify(em, never()).delete(any());
    }

    @Test
    void testDeleteAccountWithoutCards() throws Exception {
        Account account = new Account("A1", 0);

        when(em.getCreditCards(account)).thenReturn(List.of());

        accountService.delete(account);

        verify(em).delete(account);
    }

    @Test
    void testDeleteAccountWithCardDebt() throws Exception {
        Account account = new Account("A1", 0);
        CreditCard card = new CreditCard("C1");
        card.credit(50);

        when(em.getCreditCards(account)).thenReturn(List.of(card));

        assertThrows(AccountActive.class,
                () -> accountService.delete(account));

        verify(em, never()).delete(any());
        verifyNoInteractions(ccs);
    }

    @Test
    void testDeleteAccountWithCardWithoutDebt() throws Exception {
        Account account = new Account("A1", 0);
        CreditCard card = new CreditCard("C1");

        when(em.getCreditCards(account)).thenReturn(List.of(card));

        accountService.delete(account);

        verify(ccs).delete(card);
        verify(em).delete(account);
    }

    @Test
    void testDeleteById() throws Exception {
        Account account = new Account("A1", 0);

        when(em.get("A1", Account.class)).thenReturn(account);
        when(em.getCreditCards(account)).thenReturn(List.of());

        accountService.delete("A1");

        verify(em).delete(account);
    }

    @Test
    void testDeleteByIdNotFound() throws Exception {
        when(em.get("A1", Account.class))
                .thenThrow(new EntityNotFound());

        assertThrows(AccountNotFound.class,
                () -> accountService.delete("A1"));

        verify(em, never()).delete(any());
    }
}
