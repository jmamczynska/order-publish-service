window.onload = init;
var socket = new WebSocket("ws://localhost:8082/order-publish-service/actions");
socket.onmessage = onMessage;

function onMessage(event) {
	var order = JSON.parse(event.data);
	console.log("Action: " + order.action);
	if (order.action === "add") {
		printOrderElement(order);
	}
	if (order.action === "remove") {
		document.getElementById(order.id).remove();
	}
	if (order.action === "toggle") {
		var node = document.getElementById(order.id);
		var quantityText = node.children[3];
		quantityText.innerHTML = "<h3><b>Quantity: " + order.quantity
				+ "</b></h3>";
	}
}

function addOrder(id) {
	var OrderAction = {
		action : "add",
		id : id
	};
	socket.send(JSON.stringify(OrderAction));
}

function removeOrder(id) {
	var OrderAction = {
		action : "remove",
		id : id
	};
	socket.send(JSON.stringify(OrderAction));
}

function toggleOrder(element) {
	var id = element;
	var OrderAction = {
		action : "toggle",
		id : id
	};
	socket.send(JSON.stringify(OrderAction));
}

function printOrderElement(order) {
	var content = document.getElementById("content");

	var orderDiv = document.createElement("div");
	orderDiv.setAttribute("id", order.id);
	orderDiv.setAttribute("class", "order " + order.type);
	content.appendChild(orderDiv);

	var orderName = document.createElement("span");
	orderName.setAttribute("class", "orderName");
	orderName.innerHTML = order.name;
	orderDiv.appendChild(orderName);

	var orderType = document.createElement("span");
	orderType.innerHTML = "<b>Type:</b> " + order.type;
	orderDiv.appendChild(orderType);

	var orderStatus = document.createElement("span");
	orderStatus.innerHTML = "<b>Status:</b> " + order.status;
	orderDiv.appendChild(orderStatus);

	var orderQuantity = document.createElement("span");
	orderQuantity.innerHTML = "<b>Quantity:</b> " + order.quantity;
	orderDiv.appendChild(orderQuantity);

	var removeOrder = document.createElement("span");
	removeOrder.setAttribute("class", "removeOrder");
	removeOrder.innerHTML = "<a href=\"#\" OnClick=removeOrder(" + order.id
			+ ")>Remove order</a>";
	orderDiv.appendChild(removeOrder);
}

function showForm() {
	document.getElementById("addOrderForm").style.display = '';
}

function hideForm() {
	document.getElementById("addOrderForm").style.display = "none";
}

function formSubmit() {
	var form = document.getElementById("addOrderForm");
	var id = form.elements["order_type"].value;
	hideForm();
	document.getElementById("addOrderForm").reset();
	addOrder(id);
}

function init() {
	hideForm();
}