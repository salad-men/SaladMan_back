package com.kosta.saladMan.util;


public class PageInfo {
	private Integer curPage;
	private Integer allPage;
	private Integer startPage;
	private Integer endPage;
	private Integer size = 10; // 기본값

	
	public PageInfo() {}
	public PageInfo(Integer curPage) {
		this.curPage=curPage;
	}
	public PageInfo(Integer curPage, Integer size) {
		this.curPage = curPage;
		this.size = size;
	}
	
	public Integer getCurPage() {
		return curPage;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public Integer getAllPage() {
		return allPage;
	}
	public void setAllPage(Integer allPage) {
		this.allPage = allPage;
	}
	public Integer getStartPage() {
		return startPage;
	}
	public void setStartPage(Integer startPage) {
		this.startPage = startPage;
	}
	public Integer getEndPage() {
		return endPage;
	}
	public void setEndPage(Integer endPage) {
		this.endPage = endPage;
	}
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return "PageInfo [curPage=" + curPage + ", allPage=" + allPage + ", startPage=" + startPage + ", endPage="
				+ endPage + "]";
	}
	
	

}
