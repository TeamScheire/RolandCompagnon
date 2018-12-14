package be.imec.apt.bigfix.ical;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import org.apache.commons.collections4.Predicate;


/**
 * Filters out private events
 * Created by dvermeir on 08/12/2017.
 */

class PrivateEventRule<T extends Component> implements Predicate<T> {

    @Override
    public boolean evaluate(T component) {
        if (component != null) {
            Property prop = component.getProperty("CLASS");
            return prop == null || !prop.getValue().equals("PRIVATE");
        }
        return true;
    }
}