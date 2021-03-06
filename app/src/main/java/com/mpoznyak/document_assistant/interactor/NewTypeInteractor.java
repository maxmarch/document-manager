package com.mpoznyak.document_assistant.interactor;

import com.mpoznyak.data.DatabaseHelper;
import com.mpoznyak.data.repository.TypeRepository;
import com.mpoznyak.domain.command.types.AddType;
import com.mpoznyak.domain.model.Type;

public class NewTypeInteractor {

    private final DatabaseHelper mDatabaseHelper;
    private final TypeRepository mTypeRepository;
    private final AddType mAddType;

    public NewTypeInteractor(DatabaseHelper db) {
        mDatabaseHelper = db;
        mTypeRepository = new TypeRepository(mDatabaseHelper);
        mAddType = new AddType();
    }

    public void addType(Type type) {
        mAddType.execute(type, mTypeRepository);
    }

    public void update(Type type) {
        mTypeRepository.update(type);
    }
}
