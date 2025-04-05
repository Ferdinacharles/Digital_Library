package com;

public class Book {
	private int id;
    private String name;
    private String author;
    private float price;
    private int count;

    public Book(int id, String name, String author, float price, int count) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.count = count;
    }
    

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public float getPrice() {
		return price;
	}


	public void setPrice(float price) {
		this.price = price;
	}


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	@Override
	public String toString() {
		return "Book [id=" + id + ", name=" + name + ", author=" + author + ", price=" + price + ", count=" + count
				+ "]";
	}

  
    
}
