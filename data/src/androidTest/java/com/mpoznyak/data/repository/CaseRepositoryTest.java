package com.mpoznyak.data.repository;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import com.mpoznyak.data.DatabaseHelper;
import com.mpoznyak.data.specification.cases.CaseByIdSpecification;
import com.mpoznyak.domain.model.Case;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static com.mpoznyak.data.DatabaseHelper.DATABASE_NAME;
import static org.junit.Assert.assertEquals;


public class CaseRepositoryTest {


    private DatabaseHelper mDatabaseHelper;

    @Before
    public void setUp() {
        InstrumentationRegistry.getContext().deleteDatabase(DATABASE_NAME);
        mDatabaseHelper = DatabaseHelper.getInstance(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void disconnectDb() {
        mDatabaseHelper.close();
    }

    @Test
    public void testQuery() {
        Case aCase = new Case();
        aCase.setCreationDate(new Date());
        aCase.setType("type1");
        aCase.setName("case1");
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        db.execSQL("insert into types (name) Values (\'type1\')");
        db.execSQL("insert into " + DatabaseHelper.DatabaseContract.TABLE_CASES
                + " (name, type, creation_date) " + " values " + "(\'" + aCase.getName() + "\'"
                + ", " + "\'" + aCase.getType() + "\'" + ", " + "\'" + aCase.getCreationDate() + "\'"
                + ");");
        db.setTransactionSuccessful();
        CaseRepository caseRepository = new CaseRepository(mDatabaseHelper);
        List<Case> cases = caseRepository.query(new CaseByIdSpecification(1));
        assertEquals(aCase.getType(), cases.get(0).getType());
    }

}