package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulator {

	private PriorityQueue<Event> queue;
	private List<Genes> geneStudiato;
	private Graph<Genes, DefaultWeightedEdge> grafo;
	private Genes startGene;
	private int nTotIng;
	
	private int TMAX = 36;
	private double probMantenereGene = 0.3;
	
	public Simulator(Genes start, int n, Graph<Genes, DefaultWeightedEdge> graph) {
		this.startGene = start;
		this.nTotIng = n;
		this.grafo = graph;
		
		if(this.grafo.degreeOf(startGene) == 0) {
			throw new IllegalArgumentException("Vertice partenza isolato");
		}
		
		//inizializzo coda
		this.queue = new PriorityQueue<>();
		for(int nIng = 0; nIng < this.nTotIng; nIng++) {
			this.queue.add(new Event(0, n));
		}
		
		//inizializzo mondo, creando un array con nTotIng valori pari a startGene
		this.geneStudiato = new ArrayList<>();
		for(int nIng = 0; nIng < this.nTotIng; nIng++) {
			this.geneStudiato.add(startGene);
		}
		
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = queue.poll();
			
			int T = e.getT();
			int nIng = e.getnIng();
			Genes g = this.geneStudiato.get(nIng);
			
			if(T < this.TMAX) {
				//cosa studierà nIng al mese T+1?
				if(Math.random() < this.probMantenereGene) {
					//mantieni
					this.queue.add(new Event(T+1, nIng));
				}else {
					//cambia
					
					//calcola la somma dei pesi degli adiacenti, S
					double S = 0;
					for(DefaultWeightedEdge edge: this.grafo.edgesOf(g)) {
						S += this.grafo.getEdgeWeight(edge);
					}
					
					//estrai numero casuale R tra 0 e S
					double R = Math.random()*S;
					
					//confronta R con le somme parziali
					Genes nuovo = null;
					double somma = 0.0;
					
					for(DefaultWeightedEdge edge: this.grafo.edgesOf(g)) {
						somma += this.grafo.getEdgeWeight(edge);
						if(somma > R) {
							nuovo = Graphs.getOppositeVertex(this.grafo, edge, g);
							break;
						}
					}
					
					this.geneStudiato.set(nIng, nuovo);
					this.queue.add(new Event(T+1, nIng));
				}
			}
		}
	}
	
	public Map<Genes, Integer> getGeniStudiati(){
		Map<Genes, Integer> studiati = new HashMap<>();
		
		for(int nIng = 0; nIng < this.nTotIng; nIng++) {
			Genes g = this.geneStudiato.get(nIng);
			
			if(studiati.containsKey(g)) {
				studiati.put(g, studiati.get(g)+1);
			}else {
				studiati.put(g, 1);
			}
		}
		
		return studiati;
	}
}
