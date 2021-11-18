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

    c = 0
    for contact in contacts:
        c += 1
        if c % 1000 == 0:
            print(c)
        contactTime, node1, node2 = contact[0], contact[1], contact[2]
        for src_node, t_start in sources.items():

            if node1 in shortest_paths[src_node].keys():

                prime = shortest_paths[src_node][node1]

                if node1 in sinks:
                    penalty_n1 = np.abs((sources[node1]-contactTime))
                else:
                    penalty_n1 = np.abs((unreported[node1]-contactTime))

                if node2 in sinks:
                    penalty_n2 = np.abs((sources[node2]-contactTime))
                else:
                    penalty_n2 = np.abs((unreported[node2]-contactTime))

                #Set penalty to non-zero such that we don't get divide by 0 problems. contactTime is integer, so equal to minimum value.
                if penalty_n1 == 0:
                    penalty_n1 = 1
                if penalty_n2 == 0:
                    penalty_n2 = 1;

                # weighted distance to n2 from src_node
                distance_to_n2 = prime[0] + penalty_n1 + penalty_n2
                # Timestamp of current interaction. If we take the path to n2, this will be the last
                last_time_to_n2 = contactTime
                # Extend the shortest path to n1, with the edge to n2.
                path_to_n2 = prime[2] + [(contactTime, node1, node2)]
                # if n2 not in immuned or t < immuned[n2]: # if node is not immune yet
                if node2 not in shortest_paths[src_node] or shortest_paths[src_node][node2][0] >= distance_to_n2:
                    shortest_paths[src_node][node2] = (
                        distance_to_n2, last_time_to_n2, path_to_n2)

                # not in output path yet, or this is a shorter path
                # if n2 in sinks and (n2 not in out_paths[src_node] or out_paths[src_node][n2][0] >= shortest_paths[src_node][n2][0]):
                #     out_paths[src_node][n2] = copy.deepcopy(shortest_paths[src_node].get(n2, (np.Inf, -1, [])))
                # if n1 in sinks and (n1 not in out_paths[src_node] or out_paths[src_node][n1][0] >= shortest_paths[src_node][n1][0]):
                #     out_paths[src_node][n1] = copy.deepcopy(shortest_paths[src_node].get(n1, (np.Inf, -1, [])))
                if node2 in sinks and sinks[node2] >= contactTime and (node2 not in out_paths[src_node] or out_paths[src_node][node2][0] >= shortest_paths[src_node][node2][0]):
                    out_paths[src_node][node2] = copy.deepcopy(
                        shortest_paths[src_node].get(node2, (np.Inf, -1, [])))

                if node1 in sinks and sinks[node1] >= contactTime and (node1 not in out_paths[src_node] or out_paths[src_node][node1][0] >= shortest_paths[src_node][node1][0]):
                    out_paths[src_node][node1] = copy.deepcopy(
                        shortest_paths[src_node].get(node1, (np.Inf, -1, [])))

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
