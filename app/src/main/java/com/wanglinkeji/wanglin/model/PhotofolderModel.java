package com.wanglinkeji.wanglin.model;

import java.util.List;

public class PhotofolderModel {

	public static List<PhotofolderModel> list_photoFolder;

	/**
	 * 图片的文件夹路径
	 */
	private String dir;

	/**
	 * 第一张图片的路径
	 */
	private String firstImagePath;

	/**
	 * 文件夹的名称
	 */
	private String name;

	/**
	 * 图片的数量
	 */
	private int count;
	
	/**
	 * 选中图片的数量
	 */
	private int count_choosed;

	/**
	 * 是否被选中
	 */
	private boolean isChoosed;

	public boolean isChoosed() {
		return isChoosed;
	}

	public void setChoosed(boolean choosed) {
		isChoosed = choosed;
	}

	public int getCount_choosed() {
		return count_choosed;
	}

	public void setCount_choosed(int count_choosed) {
		this.count_choosed = count_choosed;
	}

	public String getDir()
	{
		return dir;
	}

	public void setDir(String dir)
	{
		this.dir = dir;
		String[] names = this.dir.split("/");
		this.name = names[names.length-1];
	}

	public String getFirstImagePath()
	{
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath)
	{
		this.firstImagePath = firstImagePath;
	}

	public String getName()
	{
		return name;
	}
	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}
	
}
