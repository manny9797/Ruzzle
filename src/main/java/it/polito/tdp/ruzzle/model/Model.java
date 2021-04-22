package it.polito.tdp.ruzzle.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.ruzzle.db.DizionarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Model {
	private final int SIZE = 4;
	private Board board;
	private List<String> dizionario;
	private StringProperty statusText;

	public Model() {
		this.statusText = new SimpleStringProperty();

		this.board = new Board(SIZE);
		DizionarioDAO dao = new DizionarioDAO();
		this.dizionario = dao.listParola();
		statusText.set(String.format("%d parole lette", this.dizionario.size()));

	}

	public void reset() {
		this.board.reset();
		this.statusText.set("Board Reset");
	}

	public Board getBoard() {
		return this.board;
	}

	public final StringProperty statusTextProperty() {
		return this.statusText;
	}

	public final String getStatusText() {
		return this.statusTextProperty().get();
	}

	public final void setStatusText(final String statusText) {
		this.statusTextProperty().set(statusText);
	}

	public List<Pos> trovaParola(String parola) { // data una parola vediamo se è presente nella matrice

		for (Pos p : board.getPositions()) { // tutte le posizioni della nostra board

			if (board.getCellValueProperty(p).get().charAt(0) == parola.charAt(0)) { // se troviamo la lettera giusta in
																						// posizione parte la ricerca
				List<Pos> percorso = new ArrayList<Pos>();
				percorso.add(p);
				if (cerca(parola, 1, percorso))
					return percorso;// se ritorna vero, la parola è stata trovata
			}
		}
		return null; // parola non trovata
	}

	private boolean cerca(String parola, int livello, List<Pos> percorso) {

		// CASO TERMINALE

		if (livello == parola.length()) {
			return true;
		}

		Pos ultima = percorso.get(percorso.size() - 1);
		List<Pos> adiacenti = board.getAdjacencies(ultima);

		for (Pos p : adiacenti) {
			if (!percorso.contains(p) && parola.charAt(livello) == board.getCellValueProperty(p).get().charAt(0)) {

				percorso.add(p);

				// faccio ricorsione
				// uscita rapida (se il cerca torna vero, è inutile continuare in un altro
				// percorso, parola trovata

				if (cerca(parola, livello + 1, percorso)) {
					return true;
				}

				// faccio backtracking

				percorso.remove(percorso.size() - 1);
			}
		}
		return false;
	}

	public List<String> trovaTutte() { // prende le parole del dizionario e fa trova parola per tutte

		List<String> tutte = new ArrayList<String>();

		for (String parola : this.dizionario) {
			parola = parola.toUpperCase();
			if (parola.length() > 1) {
				if (this.trovaParola(parola) != null) {
					tutte.add(parola);
				}
			}
		}
		return tutte;
	}

}
