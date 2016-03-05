package conversion7.trace

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class PropertyWriteListeningSupport extends PropertyChangeSupport {
    public static final String COMMON_LISTENER = "COMMON_LISTENER"

    Object source

    PropertyWriteListeningSupport(Object source) {
        super(source)
        this.source = source
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        firePropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
    }

    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        firePropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        firePropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
    }

    public void firePropertyChange(PropertyChangeEvent event) {
        String name = event.getPropertyName();

        PropertyChangeListener[] common = getPropertyChangeListeners(COMMON_LISTENER);
        PropertyChangeListener[] named = (name != null) ?
                getPropertyChangeListeners(name) :
                null;

        fire(common, event);
        fire(named, event);
    }

    static void fire(PropertyChangeListener[] listeners, PropertyChangeEvent event) {
        if (listeners != null) {
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
    }

}
