package com.chen.library.DataClass;

public class CoAuthor {
	private String no;
	private String name;
	private String more;

	public CoAuthor() {
	}

	public CoAuthor(String no, String name, String more) {
		this.no=no;
		this.name=name;
		this.more=more;
	}
	public String getNo()
	{
		return no;
	}
	public String getName()
	{
		return name;
	}
	public String getMore()
	{
		return more;
	}

}
