package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	
	private List<Genes> essentialGenes;
	private Map<String, Genes> idMap;
	private Graph<Genes, DefaultWeightedEdge> grafo;
	
	public String creaGrafo() {
		GenesDao dao = new GenesDao();
		
		this.essentialGenes = dao.getAllEssentialGenes();
		this.idMap = new HashMap<>();
		
		for(Genes g: essentialGenes) {
			this.idMap.put(g.getGeneId(), g);
		}
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, this.essentialGenes);
	
		//CREAZIONE ARCHI
		List<Interactions> archi = dao.getInteractions(idMap);
		for(Interactions arco: archi) {
			if(arco.getGene1().getChromosome() == arco.getGene2().getChromosome()) {
				Graphs.addEdge(grafo, arco.getGene1(), arco.getGene2(), Math.abs(arco.getExpressionCorr())*2.0);
			}else {
				Graphs.addEdge(grafo, arco.getGene1(), arco.getGene2(), Math.abs(arco.getExpressionCorr()));
			}
		}
		
		return String.format("Grafo creato con %d vertici e %d archi\n",  this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
		
	}
	
	public List<Genes> getEssentialGenes(){
		return essentialGenes;
	}
	
	public List<Adiacente> getGeniAdiacenti(Genes g){
		List<Genes> vicini = Graphs.neighborListOf(this.grafo, g);
		List<Adiacente> result = new ArrayList<>();
		
		for(Genes v: vicini) {
			result.add(new Adiacente(v, this.grafo.getEdgeWeight(this.grafo.getEdge(g, v))));
		}
		
		Collections.sort(result);
		return result;
	}
	
	public Map<Genes, Integer> simulaIngegneri(Genes start, int n){
		try {
			Simulator sim = new Simulator(start, n, grafo);
		
			sim.run();
		
			return sim.getGeniStudiati();
		}catch(IllegalArgumentException e) {
			return null;
		}
	}
}
