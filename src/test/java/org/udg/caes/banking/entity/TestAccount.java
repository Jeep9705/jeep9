package org.udg.caes.banking.entity;

import org.junit.jupiter.api.Test;
import org.udg.caes.banking.exceptions.NotEnoughBalance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAccount {

    @Test
    void testAccount() {
        Account account = new Account("account1", 100);

        assertEquals("account1", account.getId());
        assertEquals(100, account.getBalance());
    }

    @Test
    void testCredit() {
        Account account = new Account("account1", 100);

        account.credit(50);

        assertEquals(150, account.getBalance());
    }

    @Test
    void testDebit() throws NotEnoughBalance {
        Account account = new Account("account1", 100);

        account.debit(40);

        assertEquals(60, account.getBalance());
    }

    @Test
    void testDebitNotEnoughBalance() {
        Account account = new Account("account1", 100);

        assertThrows(NotEnoughBalance.class, () -> account.debit(150));

        assertEquals(100, account.getBalance());
    }
}