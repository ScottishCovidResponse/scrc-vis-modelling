__author__ = 'Polina'

import numpy as np
import copy


# sources: node -> time, sinks: node-> time, unreported: node -> time(default time step)
def shortestPath1(contacts, sources, sinks, unreported):
    # TS: time of transaction(unix timestamp), n1Id, n2Id, weight

    # [startOfPathNodeid][endOfPathNodeId] => (weight,Last time of path from start to end,[path]). path is triples of (time,startNodeId,endNodeId)
    shortest_paths = {i: {} for i in sources.keys()}
    out_paths = {i: {} for i in sources.keys()}
    print(out_paths)

    # set a path for self-infection to 0 weight. Note that this is never used in other paths, it's only used when this is selected as an origin node.
    for n in sources:
        contactTime = sources[n]
        shortest_paths[n][n] = (0.0, contactTime, [(contactTime, n, n)])
        out_paths[n][n] = copy.deepcopy(shortest_paths[n].get(n))

    done = False;
    while(done == False):
        done = True;#if not changed, no new shortest path was found. Horrible implementatino of shortest temporal path
        for src_node, t_start in sources.items():
            for contact in contacts:
                contactTime, cN1, cN2 = contact[0], contact[1], contact[2]
                if cN1 in shortest_paths[src_node].keys():
                    shortestPathSrcToCn1 = shortest_paths[src_node][cN1]

                    # Penalty to include this edge depending on positive test time and time of contact
                    if contact[3] != 0:
                        penalty = contact[3]; 
                        #We are using this when handling real data, where we assign weights to the likely edges. The else statement is legacy functionality
                    else:
                        if cN2 not in sinks:
                            cn2Time = unreported[cN2]
                        else:
                            cn2Time = sources[cN2]
                        penalty = cn2Time-contactTime #Penalty dependent on time difference between contact and infection of next person
                        #TODO: Note, not taking into account that the first person could no longer be infectious. Can be encoded further
                        if cn2Time < contactTime:
                            penalty =  penalty*-2;#invert and extra penalty since the contact happened after the estimated positive test date

                        # Set penalty to non-zero such that we don't get divide by 0 problems.
                        penalty = max(1,penalty);
                        
                    # weighted distance to n2 from src_node
                    distance_to_n2 = shortestPathSrcToCn1[0] +penalty
                    # Timestamp of current interaction. If we take the path to n2, this will be the last
                    last_time_to_n2 = contactTime
                    # Extend the shortest path to n1, with the edge to n2.
                    path_to_n2 = shortestPathSrcToCn1[2] + [(contactTime, cN1, cN2)]
                    # if n2 not in immuned or t < immuned[n2]: # if node is not immune yet
                    if cN2 not in shortest_paths[src_node] or shortest_paths[src_node][cN2][0] > distance_to_n2:
                        shortest_paths[src_node][cN2] = (distance_to_n2,last_time_to_n2,path_to_n2)

                    # not in output path yet, or this is a shorter path
                    if cN2 in sinks and (cN2 not in out_paths[src_node] or out_paths[src_node][cN2][0] >shortest_paths[src_node][cN2][0]):
                        done = False;
                        out_paths[src_node][cN2] = copy.deepcopy(shortest_paths[src_node].get(cN2, (np.Inf, -1, [])))

                    if cN1 in sinks and (cN1 not in out_paths[src_node] or out_paths[src_node][cN1][0] > shortest_paths[src_node][cN1][0]):
                        done = False;
                        out_paths[src_node][cN1] = copy.deepcopy(shortest_paths[src_node].get(cN1, (np.Inf, -1, [])))

    SP = {}
    for i in sources.keys():
        SP[i] = {}
        for j in sinks.keys():
            p = out_paths[i].get(j, (np.Inf, -1, []))
            if len(p[-1]) > 1:
                p = (p[0], p[1], p[2][1:])
            SP[i][j] = p
    # Return the shorests paths between sources and sinks.
    # [startOfPathNodeid][endOfPathNodeId] => (weight,Last time of path from start to end,[path]). path is triples of (time,startNodeId,endNodeId)
    # weight is infinite if no path exists.
    return SP
