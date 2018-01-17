package model;

public class OrderEvent {

	private int id;
	private String name;
	private String status;
	private String type;
	private String description;
	private int quantity;

	public OrderEvent() {
	}

	public OrderEvent(int id) {
		this.id = id;
	}

	public OrderEvent(int id, String name, String status, String type, int quantity) {
		this(id);
		this.name = name;
		this.status = status;
		this.type = type;
		this.quantity = quantity;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderEvent other = (OrderEvent) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderEvent [id=" + id + ", name=" + name + ", status=" + status + ", type=" + type + ", description="
				+ description + ", quantity=" + quantity + "]";
	}

}
