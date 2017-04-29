package com.nitika.enums;

public enum FunctionalUnit {
	FPADDER(0), FPMULTIPLIER(1), FPDIVIDER(2), INTEGERUNIT(3);
	 private int id;

    private FunctionalUnit(int val)
    {
        this.id = val;
    }

    public int getId()
    {
        return id;
    }
}
