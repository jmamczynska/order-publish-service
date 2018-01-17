package tasks;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import model.OrderEvent;

@Startup
@Singleton
public class OrderEventTask {

	@Inject
	private Logger log;

	@Inject
	private Set<OrderEvent> orderEventList;

	@Inject
	private Event<OrderEvent> event;

	@Schedule(second = "*/3", minute = "*", hour = "*", persistent = false)
	public void automaticOrderUpdate() {
		
		OrderEvent changedOrder = simulateChangeOfQuantity();
		if (changedOrder != null) {
			event.fire(changedOrder);
		}
	}

	private OrderEvent simulateChangeOfQuantity() {

		int rand = ThreadLocalRandom.current().nextInt(0, 4);

		for (OrderEvent orderEvent : orderEventList) {
			if (orderEvent.getId() == rand) {
				orderEvent.setQuantity(ThreadLocalRandom.current().nextInt(0, 1001));
				log.info("Order: " + orderEvent.getId() + " new quantity: " + orderEvent.getQuantity());
				return orderEvent;
			}
		}

		return null;
	}

}
