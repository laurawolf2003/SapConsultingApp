package de.consulting.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import de.consulting.model.Berater;
import de.consulting.service.BeraterService;

@FacesConverter(value = "beraterConverter", managed = true)
public class BeraterConverter implements Converter<Berater> {

    @Inject
    private BeraterService beraterService;

    @Override
    public Berater getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return beraterService.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Berater berater) {
        if (berater == null || berater.getId() == null) {
            return "";
        }
        return berater.getId().toString();
    }
}
