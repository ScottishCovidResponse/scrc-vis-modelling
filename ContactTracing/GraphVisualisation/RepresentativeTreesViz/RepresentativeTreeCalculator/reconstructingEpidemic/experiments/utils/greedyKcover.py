__author__ = 'Polina'
#from generator import *
from utils.paths import *
from utils.accuracy import *
#from trees import *
import uuid


def greedyBS(SP, N, K = 10, la = 0.0, ua = 0.0):
    norm = 0.0
    maxCost = 0.0
    for src, sinks in SP.items():
        for sink, info in sinks.items():
            if np.isfinite(info[0]):
                norm += info[0]
                if maxCost < info[0]:
                    maxCost = info[0]

    weights = {src: sorted([(info[0]/norm, sink, info[-1]) for sink, info in sinks.items()]) for src, sinks in SP.items()}
    #print weights.values()

    #ua = K*N*maxCost/norm
    ua = K*N*maxCost/norm
    #ua = N*maxCost/norm

    open_cost = {s: 1.0 for s in weights}

    output_paths = []

    alpha = (la + ua)/2.0
    legal_cover = {}
    legal_alpha = -1
    #alpha = ua
    #alpha = 1.0
    #for iter in xrange(100):
    iter = 0
    while iter < 100 or (ua - la) > 1e-8:
        iter += 1
        covered_nodes = set()
        actually_covered = set()
        cover = {}
        total_cost = 0.0
        while len(covered_nodes) < N:
        #for k in xrange(K, 0, -1):
            best_src, best_mgain, best_cost, best_covered_nodes = -1, np.Inf, np.Inf, set()
            for src, sinks in weights.items():
                #if src not in cover: #with or without repetition
                    m_profit = 0.0
                    cost = open_cost[src] * alpha
                    local_best_mgain, local_best_cost, local_best_covered_nodes = np.Inf, np.Inf, set()
                    local_covered_nodes = set()
                    for s in sinks:
                        if s[1] not in covered_nodes:
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
            #N -= len(best_covered_nodes)
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

                break
        alpha = (ua + la)/2.0
      #  print('alpha', la, ua, alpha)

    if not legal_cover:
        legal_cover = cover
        legal_alpha = alpha
    print('num of srcs', len(legal_cover))
    for src, sinks in legal_cover.items():
        for s in sinks:
            output_paths.append(SP[src][s][-1])
    print(total_cost)
    print(len(covered_nodes), len(actually_covered))
    return legal_cover, output_paths, total_cost, legal_alpha
