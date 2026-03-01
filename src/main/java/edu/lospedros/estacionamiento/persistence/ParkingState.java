package edu.lospedros.estacionamiento.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingState {
    private Map<Integer, ActiveTicketRecord> activeTickets = new HashMap<>();
    private List<ParkingExitRecord> exitHistory = new ArrayList<>();

    public ParkingState() {
    }

    public Map<Integer, ActiveTicketRecord> getActiveTickets() {
        return activeTickets;
    }

    public void setActiveTickets(Map<Integer, ActiveTicketRecord> activeTickets) {
        this.activeTickets = (activeTickets == null) ? new HashMap<>() : activeTickets;
    }

    public List<ParkingExitRecord> getExitHistory() {
        return exitHistory;
    }

    public void setExitHistory(List<ParkingExitRecord> exitHistory) {
        this.exitHistory = (exitHistory == null) ? new ArrayList<>() : exitHistory;
    }
}
