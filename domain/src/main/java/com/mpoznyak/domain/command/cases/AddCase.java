package com.mpoznyak.domain.command.cases;

import com.mpoznyak.domain.command.AddItem;
import com.mpoznyak.domain.model.Case;
import com.mpoznyak.domain.repository.Repository;

public class AddCase implements AddItem<Case> {

    @Override
    public void execute(Case aCase, Repository<Case> repository) {
        repository.add(aCase);
    }

}
