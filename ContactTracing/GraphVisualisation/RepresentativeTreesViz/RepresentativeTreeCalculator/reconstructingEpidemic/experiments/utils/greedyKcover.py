__author__ = 'Polina'
from utils.paths import *
from utils.accuracy import *
import uuid


def greedyBS(SP, N, K = 10, la = 0.0, ua = 0.0):
    #SP holds the shortets paths between Source and Sink nodes.
    #[startOfPathNodeid][endOfPathNodeId] => (total weight of path,Last time of path from start to end,[path]). path is triples of (time of edge,startNodeId,endNodeId)
    
    #N = number of sinks
    #K = number of paths
    
    norm = 0.0 #sum of all weights of all paths in SP
    maxCost = 0.0 #maximum weight of a path in SP
    for src, sinks in SP.items():
        for sink, info in sinks.items():
            if np.isfinite(info[0]):
                norm += info[0]
                if maxCost < info[0]:
                    maxCost = info[0]

    #weights holds a dictionary where for each nodeid, it holds the sorted list of weights to a sink
    weights = {src: sorted([(info[0]/norm, sink, info[-1]) for sink, info in sinks.items()]) for src, sinks in SP.items()}

    #maximal value
    ua = K*N*maxCost/norm

    open_cost = {s: 1.0 for s in weights}

    output_paths = []

    alpha = (la + ua)/2.0
    legal_cover = {}
    legal_alpha = -1

    iter = 0
    while iter < 100 or (ua - la) > 1e-8:
        iter += 1
        covered_nodes = set()
        actually_covered = set()
        cover = {}
        total_cost = 0.0
        while len(covered_nodes) < N:

            best_src, best_mgain, best_cost, best_covered_nodes = -1, np.Inf, np.Inf, set()
            for src, sinks in weights.items():

                    m_profit = 0.0
                    cost = open_cost[src] * alpha
                    local_best_mgain, local_best_cost, local_best_covered_nodes = np.Inf, np.Inf, set()
                    local_covered_nodes = set()
                    for s in sinks:
                        if s[1] not in covered_nodes and s[0] != np.Inf:
                            m_profit += 1.0
                            cost += s[0]
                            local_covered_nodes.add(s[1])
                            if local_best_mgain > np.divide(cost, 1.0 * m_profit):
                                local_best_mgain = np.divide(cost, 1.0 * m_profit)
                                local_best_covered_nodes = copy.deepcopy(local_covered_nodes)
                                local_best_cost = cost
                    if best_mgain > local_best_mgain:
                        best_src = src
                        best_mgain = local_best_mgain
                        best_cost = local_best_cost
                        best_covered_nodes = copy.deepcopy(local_best_covered_nodes)

            covered_nodes.update(best_covered_nodes)

            cover[best_src] = cover.get(best_src, []) + list(best_covered_nodes)
            #print('best cost', best_cost)
            if best_cost != np.Inf:
                actually_covered.update(best_covered_nodes)
            total_cost += best_cost

        #print('iteration: ', iter, 'sinks: ',N, len(actually_covered))
        #print('K: ', K, 'cover: ', len(cover), 'alpha: ', alpha)
        if len(actually_covered) < N:
            ua = alpha
        else:
            if len(cover) > K:
                la = alpha
            elif len(cover) < K:
                ua = alpha
            else:
                ua = alpha
                legal_cover = cover
                legal_alpha = alpha
                print("legal cover found")

                break
        alpha = (ua + la)/2.0

    if not legal_cover:
        legal_cover = cover
        legal_alpha = alpha
        
    print('num of srcs', len(legal_cover))
    for src, sinks in legal_cover.items(): #For every source, print the path [(time1,n1,n2),(time2,n2,n3)...] how this source gets infected
        for s in sinks:
            output_paths.append(SP[src][s][-1])
    print(total_cost)
    print(len(covered_nodes), len(actually_covered))
    return legal_cover, output_paths, total_cost, legal_alpha
