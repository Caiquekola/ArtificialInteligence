#A* Menor caminho em mapas
import copy
class Node:
    indice = -1 #cidade
    distancia_percorrida = -1
    fe = -1
    caminho = []

def funcao_heuristica(distancias, distancia_percorrida, aresta_pai_filho, indice_filho):
    fe = distancia_percorrida + distancias[indice_filho] + aresta_pai_filho
    return fe

def get_min(estados):
    
    if len(estados) == 0:
        return None
        
    melhor_filho = estados[0]
    indice_menor = 0
    for i in range(1, len(estados)):
        if estados[i].fe < melhor_filho.fe:
                melhor_filho = estados[i]
                indice_menor = i
    del estados[indice_menor]
    return melhor_filho
        

tab = [[0,1,3,0,5,0,0,0],
       [0,0,4,0,0,0,0,0],
       [0,0,0,3,0,7,0,0],
       [0,0,0,0,0,0,0,2],
       [0,0,0,0,0,0,4,0],
       [0,0,0,0,0,0,0,11],
       [0,0,0,0,0,0,0,6],
       [0,0,0,0,0,0,0,0]]
       
dist = [0,6,2,2,1,6,5,0]
 
pai = Node()
pai.indice = 0
pai.distancia_percorrida = 0
estados = []
final = Node() #estado final para retornar
