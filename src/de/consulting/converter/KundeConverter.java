package de.consulting.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import de.consulting.model.Kunde;
import de.consulting.service.KundeService;

@FacesConverter(value = "kundeConverter", managed = true)
public class KundeConverter implements Converter<Kunde> {

    @Inject
    private KundeService kundeService;

    @Override
    public Kunde getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return kundeService.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Kunde kunde) {
        if (kunde == null || kunde.getId() == null) {
            return "";
        }
        return kunde.getId().toString();
    }
}
