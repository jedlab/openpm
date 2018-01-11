package com.jedlab.framework.audit;

import org.hibernate.envers.RevisionType;

/**
 * @author omidbiz
 *
 * @param <T>
 */
public class Revision<T>
{

	private T instance;
	private RevisionPO revpo;
	private RevisionType rt;

	public Revision(T t, RevisionPO revpo, RevisionType rt)
	{
		this.instance = t;
		this.revpo = revpo;
		this.rt = rt;
	}

	public T getInstance()
	{
		return instance;
	}

	public RevisionPO getRevpo()
	{
		return revpo;
	}

	public RevisionType getRt()
	{
		return rt;
	}

}
