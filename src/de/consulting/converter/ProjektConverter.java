package de.consulting.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import de.consulting.model.Projekt;
import de.consulting.service.ProjektService;

@FacesConverter(value = "projektConverter", managed = true)
public class ProjektConverter implements Converter<Projekt> {

    @Inject
    private ProjektService projektService;

    @Override
    public Projekt getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return projektService.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Projekt projekt) {
        if (projekt == null || projekt.getId() == null) {
            return "";
        }
        return projekt.getId().toString();
    }
}
