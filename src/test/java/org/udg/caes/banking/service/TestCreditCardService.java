
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestCreditCardService {

    @Mock
    EntityManager em;

    @InjectMocks
    CreditCardService creditCardService;

    @Test
    void testCredit() throws Exception {
        CreditCard card = new CreditCard("CC1");

        creditCardService.credit(card, 0);

        assertEquals(0, card.getCredit());
        verify(em).persist(card);
    }

    @Test
    void testCreditExceeded() throws Exception {
        CreditCard card = new CreditCard("CC1");

        assertThrows(CreditExceeded.class,
                () -> creditCardService.credit(card, 50));

        assertEquals(0, card.getCredit());
        verify(em, never()).persist(any());
    }

    @Test
    void testCharge() throws Exception {
        CreditCard card = new CreditCard("CC1");
        Account account = new Account("A1", 100);

        card.credit(40);

        creditCardService.charge(card, account);

        assertEquals(60, account.getBalance());
        assertEquals(0, card.getCredit());

        verify(em).persist(card);
        verify(em).persist(account);
    }

    @Test
    void testChargeNotEnoughBalance() throws Exception {
        CreditCard card = new CreditCard("CC1");
        Account account = new Account("A1", 20);

        card.credit(50);

        assertThrows(NotEnoughBalance.class,
                () -> creditCardService.charge(card, account));

        assertEquals(20, account.getBalance());
        assertEquals(50, card.getCredit());

        verify(em, never()).persist(any());
    }

    @Test
    void testDelete() throws Exception {
        CreditCard card = new CreditCard("CC1");
        Account account = new Account("A1", 100);

        card.credit(30);

        when(em.getAccountAssociated(card)).thenReturn(account);

        creditCardService.delete(card);

        assertEquals(70, account.getBalance());
        assertEquals(0, card.getCredit());

        verify(em).persist(card);
        verify(em).persist(account);
        verify(em).delete(card);
    }

    @Test
    void testDeleteNotEnoughBalance() throws Exception {
        CreditCard card = new CreditCard("CC1");
        Account account = new Account("A1", 10);

        card.credit(50);

        when(em.getAccountAssociated(card)).thenReturn(account);

        assertThrows(NotEnoughBalance.class,
                () -> creditCardService.delete(card));

        assertEquals(10, account.getBalance());
        assertEquals(50, card.getCredit());

        verify(em, never()).delete(any());
        verify(em, never()).persist(any());
    }

    @Test
    void testInvalidate() throws Exception {
        CreditCard card = new CreditCard("CC1");

        creditCardService.invalidate(card);

        verify(em).persist(card);
    }
}
