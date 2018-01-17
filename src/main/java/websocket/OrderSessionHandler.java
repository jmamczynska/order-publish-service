package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import model.OrderEvent;

@ApplicationScoped
public class OrderSessionHandler {

	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<OrderEvent> orderEvents = Collections.synchronizedSet(new HashSet<OrderEvent>());
	private static Map<Integer, List<Session>> ordersPerSession = Collections
			.synchronizedMap(new HashMap<Integer, List<Session>>());

	private int orderId = 0;

	@Inject
	private Logger log;

	@PostConstruct
	public void init() {
		orderEvents.add(new OrderEvent(orderId++, "WIG20", "On", "Type1", 200));
		orderEvents.add(new OrderEvent(orderId++, "ZYWIEC", "On", "Type2", 80));
		orderEvents.add(new OrderEvent(orderId++, "VISTULA", "On", "Type3", 142));
		orderEvents.add(new OrderEvent(orderId++, "REMAK", "Off", "Type4", 0));
	}

	@Produces
	public Set<OrderEvent> getOrderEvents() {
		return orderEvents;
	}

	public void addSession(Session session) {
		sessions.add(session);
	}

	public void removeSession(Session session) {
		sessions.remove(session);

		// remove sessions from ordersPerSession
		Iterator<Map.Entry<Integer, List<Session>>> it = ordersPerSession.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, List<Session>> entry = it.next();

			Iterator<Session> sessionIt = entry.getValue().iterator();
			Session s = null;
			while (sessionIt.hasNext()) {
				s = sessionIt.next();
				if (s.getId().equals(session.getId())) {
					sessionIt.remove();
				}
			}
		}
	}

	public Session getSession(String sessionId) {
		for (Session session : sessions) {
			if (session.getId().equals(sessionId)) {
				return session;
			}
		}
		return null;
	}

	public void addOrderEvent(OrderEvent orderEvent, Session session) {

		if (ordersPerSession.get(orderEvent.getId()) == null) {
			ordersPerSession.put(orderEvent.getId(), new ArrayList<>());
		}

		ordersPerSession.get(orderEvent.getId()).add(session);
		OrderEvent selectedOrder = getOrderEventById(orderEvent.getId());

		JsonObject addMessage = createAddMessage(selectedOrder);
		sendToSession(session, addMessage);
		log.info("Order event added to session");
	}

	public void updateOrder(@Observes OrderEvent orderEvent) {

		List<Session> sessions = ordersPerSession.get(orderEvent.getId());
		if (sessions != null) {
			JsonObject updateOrderMessage = createUpdateMessage(orderEvent);
			for (Session session : sessions) {
				sendToSession(session, updateOrderMessage);
			}
		}
	}

	public void removeOrder(int orderId, Session session) {

		removeSessionFromOrderEventMapByOrderId(orderId, session);
		JsonObject removeMessage = createRemoveMessage(getOrderEventById(orderId));
		sendToSession(session, removeMessage);
	}

	public void removeSessionFromOrderEventMapByOrderId(int orderId, Session session) {

		Iterator<Session> sessionListIter = ordersPerSession.get(orderId).iterator();
		Session temp = null;
		while (sessionListIter.hasNext()) {
			temp = sessionListIter.next();
			if (temp.equals(session)) {
				sessionListIter.remove();
				log.info("OrderEvent " + orderId + " removed from session: " + session.getId());
				return;
			}
		}
	}

	public void toggleOrder(int id) {

	}

	// utils

	private OrderEvent getOrderEventById(int id) {
		for (OrderEvent order : orderEvents) {
			if (order.getId() == id) {
				return order;
			}
		}

		return null;
	}

	private JsonObject createAddMessage(OrderEvent orderEvent) {
		JsonProvider provider = JsonProvider.provider();
		JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("id", orderEvent.getId())
				.add("name", orderEvent.getName()).add("type", orderEvent.getType())
				.add("status", orderEvent.getStatus()).add("quantity", orderEvent.getQuantity())
				.add("description", "Description").build();
		return addMessage;
	}

	private JsonObject createUpdateMessage(OrderEvent orderEvent) {
		JsonProvider provider = JsonProvider.provider();
		JsonObject addMessage = provider.createObjectBuilder().add("action", "toggle").add("id", orderEvent.getId())
				.add("name", orderEvent.getName()).add("type", orderEvent.getType()).add("status", orderEvent.getStatus())
				.add("quantity", orderEvent.getQuantity()).build();
		return addMessage;
	}

	private JsonObject createRemoveMessage(OrderEvent orderEvent) {
		JsonProvider provider = JsonProvider.provider();
		JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove").add("id", orderEvent.getId())
				.build();
		return removeMessage;
	}

	@SuppressWarnings("unused")
	private void sendToAllConnectedSessions(JsonObject message) {
		for (Session session : sessions) {
			sendToSession(session, message);
		}
	}

	private void sendToSession(Session session, JsonObject message) {
		try {
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException ex) {
			removeSession(session);
			log.warning("Cannot pass message to session. Session removed");
		}
	}

}
