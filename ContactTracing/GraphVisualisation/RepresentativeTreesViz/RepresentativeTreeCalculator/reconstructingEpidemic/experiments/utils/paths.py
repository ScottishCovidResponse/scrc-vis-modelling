__author__ = 'Polina'

import numpy as np
import copy

def shortestPath1(TS, sources, sinks, immuned, unreported, parameter = 1.0): #src: node -> time, sink: node-> time, immuned: node -> time

    shortest_paths = {i: {} for i in sources.keys()}
    out_paths = {i: {} for i in sources.keys()}
    c = 0
    for interaction in TS:
        c += 1
        if c % 1000 == 0:
            print (c)
        t, n1, n2 = interaction[0], interaction[1], interaction[2]
        for src_node, t_start in sources.items():

            #if (n1 not in immuned or t < immuned[n1]):
            if n1 == src_node:
                dn1 = 0.0
                if src_node not in shortest_paths[src_node] or shortest_paths[src_node][n1][0] >= dn1:
                    shortest_paths[src_node][src_node] = (dn1, t, [(t, n1, n1)])

            if n1 in shortest_paths[src_node].keys():

                prime = shortest_paths[src_node][n1]

                if n1 in sinks:                        
                    penalty_n1 = parameter*0.5*np.abs((sources[n1]-t).total_seconds())
                else:
                    penalty_n1 = parameter*0.5*np.abs((unreported[n1]-t).total_seconds())
                if n2 in sinks:
                    penalty_n2 = parameter*0.5*np.abs((sources[n2]-t).total_seconds())
                else:
                    penalty_n2 = parameter*0.5*np.abs((unreported[n2]-t).total_seconds())
                
                dn2 = prime[0] + penalty_n1 + penalty_n2
                pathn2 = prime[2] + [(t, n1, n2)]
                an2 = t
                #if n2 not in immuned or t < immuned[n2]: # if node is not immune yet
                if n2 not in shortest_paths[src_node] or shortest_paths[src_node][n2][0] >= dn2:
                    shortest_paths[src_node][n2] = (dn2, an2, pathn2)
                        
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
    #return {i: {j: out_paths[i].get(j, (np.Inf, -1, [])) for j in sinks.keys()} for i in sources.keys()}
    return SP