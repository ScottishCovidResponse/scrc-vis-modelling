__author__ = 'Polina'

import numpy as np
import copy

def shortestPath1(TS, sources, sinks, unreported): #src: node -> time, sink: node-> time, immuned: node -> time

    shortest_paths = {i: {} for i in sources.keys()} #[startOfPathNodeid][endOfPathNodeId] => (weight,Last time of path from start to end,[path]). path is triples of (time,startNodeId,endNodeId)
    out_paths = {i: {} for i in sources.keys()}
    print(out_paths)
    
    
    for n in sources:
        t = sources[n]
        shortest_paths[n][n] = (0.0, t, [(t, n, n)])
        out_paths[n][n] = copy.deepcopy(shortest_paths[n].get(n))
    
    c = 0
    for interaction in TS:
        c += 1
        if c % 1000 == 0:
            print (c)
        t, n1, n2 = interaction[0], interaction[1], interaction[2]
        for src_node, t_start in sources.items():

            if n1 in shortest_paths[src_node].keys():

                prime = shortest_paths[src_node][n1]

                if n1 in sinks:                        
                    penalty_n1 = 0.5*np.abs((sources[n1]-t))
                else:
                    penalty_n1 = 0.5*np.abs((unreported[n1]-t))
                if n2 in sinks:
                    penalty_n2 = 0.5*np.abs((sources[n2]-t))
                else:
                    penalty_n2 = 0.5*np.abs((unreported[n2]-t))
                
                distance_to_n2 = prime[0] + penalty_n1 + penalty_n2 #weighted distance to n2 from src_node
                last_time_to_n2 = t #Timestamp of current interaction. If we take the path to n2, this will be the last
                path_to_n2 = prime[2] + [(t, n1, n2)] #Extend the shortest path to n1, with the edge to n2.
                #if n2 not in immuned or t < immuned[n2]: # if node is not immune yet
                if n2 not in shortest_paths[src_node] or shortest_paths[src_node][n2][0] >= distance_to_n2:
                    shortest_paths[src_node][n2] = (distance_to_n2, last_time_to_n2, path_to_n2)
                        
                #not in output path yet, or this is a shorter path
                # if n2 in sinks and (n2 not in out_paths[src_node] or out_paths[src_node][n2][0] >= shortest_paths[src_node][n2][0]):
                #     out_paths[src_node][n2] = copy.deepcopy(shortest_paths[src_node].get(n2, (np.Inf, -1, [])))                        
                # if n1 in sinks and (n1 not in out_paths[src_node] or out_paths[src_node][n1][0] >= shortest_paths[src_node][n1][0]):
                #     out_paths[src_node][n1] = copy.deepcopy(shortest_paths[src_node].get(n1, (np.Inf, -1, [])))
                if n2 in sinks and sinks[n2] >= t and (n2 not in out_paths[src_node] or out_paths[src_node][n2][0] >= shortest_paths[src_node][n2][0]):
                    out_paths[src_node][n2] = copy.deepcopy(shortest_paths[src_node].get(n2, (np.Inf, -1, [])))                        
                
                if n1 in sinks and sinks[n1] >= t and (n1 not in out_paths[src_node] or out_paths[src_node][n1][0] >= shortest_paths[src_node][n1][0]):
                    out_paths[src_node][n1] = copy.deepcopy(shortest_paths[src_node].get(n1, (np.Inf, -1, [])))
                    
    SP = {}
    for i in sources.keys():
        SP[i] = {}
        for j in sinks.keys():
            p = out_paths[i].get(j, (np.Inf, -1, []))
            if len(p[-1]) > 1:
                p = (p[0], p[1], p[2][1:])
            SP[i][j] = p
    #Return the shorests paths between sources and sinks.
    #[startOfPathNodeid][endOfPathNodeId] => (weight,Last time of path from start to end,[path]). path is triples of (time,startNodeId,endNodeId)
    #weight is infinite if no path exists.
    return SP