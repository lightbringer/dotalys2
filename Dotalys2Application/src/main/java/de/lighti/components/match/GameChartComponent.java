package de.lighti.components.match;

import java.awt.Color;

import javax.swing.BorderFactory;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import de.lighti.io.ChartCreator;
import de.lighti.model.AppState;

public class GameChartComponent extends ChartPanel {
    public enum Mode {
        AVERAGE_TEAM_DISTANCE, TEAM_XP, TEAM_GOLD
    }

    private final AppState appState;

    public GameChartComponent( AppState appState ) {
        super( null );
        this.appState = appState;
        setBackground( Color.WHITE );
        setBorder( BorderFactory.createEtchedBorder() );
    }

    public void setMode( Mode m ) {
        JFreeChart data;

        switch (m) {
            case AVERAGE_TEAM_DISTANCE:
                data = ChartCreator.createAverageTeamDistanceGraph( appState );
                break;
            case TEAM_XP:
                data = ChartCreator.createTeamXpDifferenceGraph( appState );
                break;
            case TEAM_GOLD:
                data = ChartCreator.createTeamGoldDifferenceGraph( appState );
                break;
            default:
                throw new IllegalArgumentException();
        }

        setChart( data );
    }

}
