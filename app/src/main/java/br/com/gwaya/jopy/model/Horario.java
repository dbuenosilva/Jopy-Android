package br.com.gwaya.jopy.model;

import java.util.Date;

/**
 * Created by pedro.sousa on 09/06/15.
 */
public class Horario {

    private String horario = new Date().toString();

    public Horario(String horario) {
        this.horario = horario;
    }

    public Horario() {

    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }
}
