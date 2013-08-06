package com.discover.mobile.bank.statements;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatementList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1676351360417305316L;

	public List<Statement> statementList;
	
	/*
	 * This function orders the statements in 
	 * reverse chronological order
	 */
	public void orderStatements () {
		Collections.sort(statementList);
	}
	
}
