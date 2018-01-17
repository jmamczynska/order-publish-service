package websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import model.OrderEvent;

@ApplicationScoped
@ServerEndpoint(value = "/actions")
public class OrderWebSocketServer {

	@Inject
	private Logger log;

	@Inject
	private OrderSessionHandler sessionHandler;

	@OnOpen
	public void open(Session session) {
		sessionHandler.addSession(session);
	}

	@OnMessage
	public void handleMessage(String message, Session session) {
		log.info("handleMessage");
		try (JsonReader reader = Json.createReader(new StringReader(message))) {
			JsonObject jsonMessage = reader.readObject();

			if ("add".equals(jsonMessage.getString("action"))) {
				String id = jsonMessage.getString("id");
				OrderEvent order = new OrderEvent();
				order.setId(Integer.valueOf(id));
				sessionHandler.addOrderEvent(order, session);
			}

			if ("remove".equals(jsonMessage.getString("action"))) {
				int id = (int) jsonMessage.getInt("id");
				sessionHandler.removeOrder(id, session);
			}

			if ("toggle".equals(jsonMessage.getString("action"))) {
				int id = (int) jsonMessage.getInt("id");
				sessionHandler.toggleOrder(id);
			}
		}
	}

	@OnClose
	public void close(Session session) {
		sessionHandler.removeSession(session);
	}

	@OnError
	public void onError(Throwable error) {
		Logger.getLogger(OrderWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
	}

}
