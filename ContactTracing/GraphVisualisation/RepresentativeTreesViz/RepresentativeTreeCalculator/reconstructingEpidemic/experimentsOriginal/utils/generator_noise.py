__author__ = 'Polina'
import networkx as nx
from datetime import datetime, timedelta
import time
import numpy as np
import matplotlib.pyplot as plt
import pickle
import os.path
import copy
import sys
from ast import literal_eval

def generate_SI(G, infP = 0.3, N = 100,  seeds_num = 3):
    Gcc = sorted(nx.connected_component_subgraphs(G.to_undirected()), key = len, reverse=True)
    G = nx.Graph()
    #print 'components', len(Gcc)
    nodes = set()
    i = 0
    seeds = []
    leaf = []
    infection_order = []
    infection_track = {}
    while len(nodes) < N and i < len(Gcc):
        G0 = Gcc[i]
        #print 'nodes', nx.number_of_nodes(G0)
        i += 1
        tmp = list(set(G0.nodes()).difference(nodes))
        #sidx = np.random.choice(range(len(tmp)), size = seeds_num)
        s = [tmp[j] for j in np.random.choice(range(len(tmp)), size = seeds_num)]
        ##print s
        seeds = seeds + list(s)
        nodes.update(set(s))
        this_comp = set()
        this_comp.update(set(s))
        for i in s:
            infection_track[i] = []
        while len(this_comp) < G0.number_of_nodes() and len(nodes) < N:
            new_inf = set()
            L = len(nodes)
            for i in nodes:
                for j in G0.neighbors_iter(i):
                    if j not in nodes and j not in new_inf and np.random.rand() < infP:
                        new_inf.add(j)
                        this_comp.add(j)
                        infection_order.append((i,j))
                        infection_track[i] = infection_track.get(i,[]) + [j]
                if L + len(new_inf) >= N:
                    break
            nodes.update(new_inf)
    return infection_order, infection_track, seeds, nodes


def generate_SP(G, N = 100, seeds_num = 3):
    Gcc = sorted(nx.connected_component_subgraphs(G.to_undirected()), key = len, reverse = True)
    nodes = set()
    i = 0
    seeds = set()
    leaf = []
    infection_order = []
    infection_track = {}

    for i in range(len(Gcc)):
        nodes.update(set(Gcc[i].nodes()))
        seeds.add(Gcc[i].nodes()[np.random.choice(range(Gcc[i].number_of_nodes()))])
        if len(nodes) >= N:
            break
    G = G.subgraph(nodes).to_undirected()
    #if len(seeds) < seeds_num:
    rest_seeds_num = max(0, seeds_num - len(seeds))

    nodes = list(nodes.difference(seeds))


    chosen = [nodes[j] for j in np.random.choice(range(len(nodes)), size = N)]
    seeds.update(chosen[:rest_seeds_num])
    to_infect = chosen[rest_seeds_num:]

    #seeds_num = len(seeds)

    SSSP = {}
    for s in seeds:
        SPs = nx.single_source_shortest_path(G, s)
        SSSP[s] = SPs

    inf_paths = []
    maxpath = -sys.maxint

    for i in to_infect:
        src = -1
        for s in SSSP.keys():
            if (src == -1 and i in SSSP[s]) or (i in SSSP[s] and len(SSSP[s][i]) < len(SSSP[src][i])):
                src = s

        infection_track[src] = infection_track.get(src, []) + [i]
        if len(SSSP[src][i]) == 1:
            inf_paths.append([(src, src)])
        else:
            inf_paths.append([tuple(SSSP[src][i][ind:ind+2]) for ind in range(len(SSSP[src][i])-1)])
        if len(SSSP[src][i]) > maxpath:
            maxpath = len(SSSP[src][i])-1

    for ind in range(maxpath):
        for path in inf_paths:
            if ind < len(path) and path[ind] not in infection_order:
                infection_order.append(path[ind])

    return infection_order, infection_track, seeds, to_infect

def generate_IC(G, infP = 0.3, N = 100,  seeds_num = 3):
    Gcc = sorted(nx.connected_component_subgraphs(G.to_undirected()), key = len, reverse=True)
    G = nx.Graph()
    #print 'components', len(Gcc)
    nodes = set()
    i = 0
    seeds = []
    leaf = []
    infection_order = []
    infection_track = {}
    while len(nodes) < N and i < len(Gcc):
        G0 = Gcc[i]
        #print 'nodes', nx.number_of_nodes(G0)
        i += 1
        tmp = list(set(G0.nodes()).difference(nodes))
        #sidx = np.random.choice(range(len(tmp)), size = seeds_num)
        s = [tmp[j] for j in np.random.choice(range(len(tmp)), size = seeds_num)]
        ##print s
        seeds = seeds + list(s)
        nodes.update(set(s))
        this_comp = set()
        this_comp.update(set(s))
        last_active = nodes
        for i in s:
            infection_track[i] = []
        while len(this_comp) < G0.number_of_nodes() and len(nodes) < N and len(last_active) > 1:
            new_inf = set()
            L = len(nodes)
            #for i in nodes:
            for i in last_active:
                for j in G0.neighbors_iter(i):
                    if j not in nodes and j not in new_inf and np.random.rand() < infP:
                        new_inf.add(j)
                        this_comp.add(j)
                        infection_order.append((i,j))
                        infection_track[i] = infection_track.get(i,[]) + [j]
                if L + len(new_inf) >= N:
                    break
            last_active = set()
            last_active.update(new_inf)
            nodes.update(new_inf)
    return infection_order, infection_track, seeds, nodes

def generate_graph(type = 'PL', n = 100, seed = 1.0, parameter = 2.1):
    if type == 'ER':
        G = nx.erdos_renyi_graph(n, p=parameter, seed=seed, directed=True)
        G = nx.DiGraph(G)
        G.remove_edges_from(G.selfloop_edges())
    elif type == 'PL':
        z = nx.utils.create_degree_sequence(n, nx.utils.powerlaw_sequence, exponent = parameter)
        while not nx.is_valid_degree_sequence(z):
            z = nx.utils.create_degree_sequence(n, nx.utils.powerlaw_sequence, exponent = parameter)
        G = nx.configuration_model(z)
        G = nx.DiGraph(G)
        G.remove_edges_from(G.selfloop_edges())
    elif type == 'BA':
        G = nx.barabasi_albert_graph(n, 3, seed=None)
        G = nx.DiGraph(G)
    elif type == 'grid':
        G = nx.grid_2d_graph(int(np.ceil(np.sqrt(n))), int(np.ceil(np.sqrt(n))))
        G = nx.DiGraph(G)
    elif type in ['facebook', 'enron', 'twitter', 'students', 'tumblr', 'facebookBig']:
        #print 'start reading'
        #_, G, _, _ = readRealGraph(os.path.join("..","..","Data", type+".txt"))
        _, G, _, _ = readRealGraph(os.path.join("..","Data", type+".txt"))
        print ('size of graph', G.number_of_nodes())
        #Gcc = sorted(nx.connected_component_subgraphs(G.to_undirected()), key = len, reverse=True)
        #print Gcc[0].number_of_nodes()
        #print 'num of connected components', len(sorted(nx.connected_component_subgraphs(G.to_undirected()), key = len, reverse=True))
        #exit()
        if G.number_of_nodes() > n:
            G = getSubgraph(G, n)
        #G = getSubgraphSimulation(G, n, infP = 0.3)
    #nx.draw(G)
    #plt.show()
    return G


def get_noisy_TS_equiprob(type='PL', n = 100, inf_fraction = 0.5, seeds_num = 2, noise = 2, reportP = 0.5):
    stored_moments = {}

    G = generate_graph(type = type, n = n)

    #print 'generated graph:', G.number_of_nodes(), G.number_of_edges()

    infection_order, infection_track, seeds, nodes = generate_SI(G, infP = 0.1, N = n*inf_fraction, seeds_num = seeds_num)
    st = datetime(2000, 1, 1, 0, 0, 0)
    i = 0
    TS = []
    noisy_idx = np.random.choice(range(G.number_of_edges()), size = noise)
    edges = G.edges()
    noisy_interactions = [edges[e] for e in noisy_idx]
    for e in noisy_interactions:
        t = st + timedelta(seconds = i)
        TS.append([t, e[0], e[1], 0, 0, 0, 0, 0, 0])

        i += 1
    infected = set()
    for interaction in infection_order:
        reported1, external1 = 0, 0
        reported2, external2 = 0, 0
        n1Inf, n2Inf = 1, 1
        t = st + timedelta(seconds = i)
        e = interaction
        if e[0] in seeds:
            #reported1 = 1
            external1 = 1

        if e[0] not in stored_moments:
            stored_moments[e[0]] = []
        if e[1] not in stored_moments:
            stored_moments[e[1]] = []
        stored_moments[e[0]].append(i)
        stored_moments[e[1]].append(i)


        infected.add(e[0])
        infected.add(e[1])
        TS.append([t, e[0], e[1], n1Inf, n2Inf, reported1, reported2, external1, external2])
        i += 1
        
        noisy_idx = np.random.choice(range(G.number_of_edges()), size = noise)
        noisy_interactions = [edges[e] for e in noisy_idx]
        for e in noisy_interactions:
            t = st + timedelta(seconds = i)
            n1Inf = 1 if e[0] in infected else 0
            n2Inf = 1 if e[1] in infected  else 0
            TS.append([t, e[0], e[1], n1Inf, n2Inf, 0, 0, 0, 0])

            i += 1

    counter = 0
    #print len(infected)

    for k, v in stored_moments.items():
        if np.random.rand() <= reportP:
        #if np.random.rand() <= 1.0:
            idx = np.random.randint(0, high=len(v))
            ##print v[idx]
            if TS[v[idx]][1] == k:
                TS[v[idx]][5] = 1
            else:
                TS[v[idx]][6] = 1
            counter += 1

    #print 'reportes:', counter

    c = 0
    for i in TS:
        if i[5] == 1:
            c += 1
        if i[6] == 1:
            c += 1
    #print c
    #exit()
    #name = 'generated'
    #f = open(name + '.txt', 'w')
    #for i in TS:
    #    f.write(str(i[0]) + '\t' + '\t'.join(map(str, i[1:]))+'\n')
    #f.close()
    return TS, G, infection_order, infection_track, seeds, nodes


def get_noisy_TS_leaves_with_P(type = 'PL', n = 100, inf_fraction = 0.5, seeds_num = 2, noise = 2, reportP = 0.5, model = 'SI', bias = 1.0, parameter = 2.1):
    #name = 'generated'
    #f = open(name + '.txt', 'w')

    G = generate_graph(type = type, n = n, parameter = parameter)

    if model == 'SI':
        infection_order, infection_track, seeds, nodes = generate_SI(G, infP = 0.1, N = n*inf_fraction, seeds_num = seeds_num)
    elif model == 'SI_1':
        infection_order, infection_track, seeds, nodes = generate_SI(G, infP = 1.0, N = n*inf_fraction, seeds_num = seeds_num)
    elif model == 'SP':
        infection_order, infection_track, seeds, nodes = generate_SP(G, N = n*inf_fraction, seeds_num = seeds_num)
    elif model == 'IC':
        infection_order, infection_track, seeds, nodes = generate_IC(G, infP = 0.8, N = 100,  seeds_num = 3)

    leaves = []
    for k,v in infection_track.items():
        for i in v:
            if i not in infection_track.keys():
                leaves.append(i)
    ##print len(leaves)
    st = datetime(2000, 1, 1, 0, 0, 0)
    i = 0
    TS = []

    edges = G.edges()
    enum = [1.0 if e in infection_order else bias for e in edges ]
    enum = [e/sum(enum) for e in enum]
    #e for e in edges if e in infection_order
    noisy_idx = np.random.choice(range(G.number_of_edges()), size = noise, p = enum)
    noisy_interactions = [edges[e] for e in noisy_idx]
    for e in noisy_interactions:
        t = st + timedelta(seconds = i)
        TS.append([t, e[0], e[1], 0, 0, 0, 0, 0, 0])
        #f.write('\t'.join([str(t), str(e[0]), str(e[1]), str(0), str(0), str(0), str(0),
        #                  str(0), str(0)])+'\n')
        i += 1
    infected = set()
    for interaction in infection_order:
        reported1, external1 = 0, 0
        reported2, external2 = 0, 0
        n1Inf, n2Inf = 1, 1
        t = st + timedelta(seconds = i)
        i += 1
        e = interaction
        if e[0] in seeds:
            #reported1 = 1
            external1 = 1
        if e[1] in leaves and np.random.rand() <= reportP:
            reported2 = 1
        infected.add(e[0])
        infected.add(e[1])
        TS.append([t, e[0], e[1], n1Inf, n2Inf, reported1, reported2, external1, external2])
        #f.write('\t'.join([str(t), str(e[0]), str(e[1]), str(n1Inf), str(n2Inf), str(reported1), str(reported2),
        #                  str(external1), str(external2)])+'\n')

        #noisy_interactions = np.random.choice(G, size = noise)
        noisy_idx = np.random.choice(range(G.number_of_edges()), size = noise)
        noisy_interactions = [edges[e] for e in noisy_idx]
        for e in noisy_interactions:
            t = st + timedelta(seconds = i)
            n1Inf = 1 if e[0] in infected else 0
            n2Inf = 1 if e[1] in infected  else 0
            TS.append([t, e[0], e[1], n1Inf, n2Inf, 0, 0, 0, 0])
            #f.write('\t'.join([str(t), str(e[0]), str(e[1]), str(n1Inf), str(n2Inf), str(0), str(0),
            #                  str(0), str(0)])+'\n')
            i += 1

    #f.close()
    return TS, G, infection_order, infection_track, seeds, nodes

def get_to_be_reported(nodes, infection_track, reportP = 0.6, frac_leaves = 0.7):
    leaves = set(nodes).difference(set(infection_track.keys()))
    not_leaves = set(infection_track.keys())
    to_be_reported = []
    to_be_reported += list(np.random.choice(list(leaves), int(len(leaves)*reportP*frac_leaves)))
    to_be_reported += list(np.random.choice(list(not_leaves), int(len(not_leaves)*reportP*(1.0 - frac_leaves))))
    return to_be_reported

def get_noisy_TS_empty_reports(type='PL', n = 100, inf_fraction = 0.5, seeds_num = 2, noise = 2, pad = 100, model = 'SI', parameter = 2.1):

    G = generate_graph(type = type, n = n, parameter = parameter)

    if model == 'SI':
        infection_order, infection_track, seeds, nodes = generate_SI(G, infP = 0.1, N = n*inf_fraction, seeds_num = seeds_num)
    elif model == 'SI_1':
        infection_order, infection_track, seeds, nodes = generate_SI(G, infP = 1.0, N = n*inf_fraction, seeds_num = seeds_num)
    elif model == 'SP':
        infection_order, infection_track, seeds, nodes = generate_SP(G, N = n*inf_fraction, seeds_num = seeds_num)
    elif model == 'IC':
        infection_order, infection_track, seeds, nodes = generate_IC(G, infP = 0.8, N = 100,  seeds_num = 3)
		
    st = datetime(2000, 1, 1, 0, 0, 0)
    i = 0
    TS = []
    noisy_idx = np.random.choice(range(G.number_of_edges()), size = noise)
    edges = G.edges()
    noisy_interactions = [edges[e] for e in noisy_idx]
    for e in noisy_interactions:
        t = st + timedelta(seconds = i)
        TS.append([t, e[0], e[1], 0, 0, 0, 0, 0, 0])

        i += 1
    infected = set()
    for interaction in infection_order:
        reported1, external1 = 0, 0
        reported2, external2 = 0, 0
        n1Inf, n2Inf = 1, 1
        t = st + timedelta(seconds = i)
        e = interaction
        if e[0] in seeds:
            #reported1 = 1
            external1 = 1

        infected.add(e[0])
        infected.add(e[1])
        TS.append([t, e[0], e[1], n1Inf, n2Inf, reported1, reported2, external1, external2])
        i += 1

        noisy_idx = np.random.choice(range(G.number_of_edges()), size = noise)
        noisy_interactions = [edges[e] for e in noisy_idx]
        for e in noisy_interactions:
            t = st + timedelta(seconds = i)
            n1Inf = 1 if e[0] in infected else 0
            n2Inf = 1 if e[1] in infected  else 0
            TS.append([t, e[0], e[1], n1Inf, n2Inf, 0, 0, 0, 0])

            i += 1
    #add padding
    noisy_idx = np.random.choice(range(G.number_of_edges()), size = max(0, pad - noise))
    noisy_interactions = [edges[e] for e in noisy_idx]
    for e in noisy_interactions:
        t = st + timedelta(seconds = i)
        n1Inf = 1 if e[0] in infected else 0
        n2Inf = 1 if e[1] in infected  else 0
        TS.append([t, e[0], e[1], n1Inf, n2Inf, 0, 0, 0, 0])
        i += 1

    counter = 0
    #print len(infected)

    #name = 'generated'
    #f = open(name + '.txt', 'w')
    #for i in TS:
    #    f.write(str(i[0]) + '\t' + '\t'.join(map(str, i[1:]))+'\n')
    #f.close()
    return TS, G, infection_order, infection_track, seeds, nodes

def getSubgraph(G, N = 1000):
    nodes = set()
    while len(nodes) < N:
        s = np.random.choice(G.nodes())
        nodes.add(s)
        for edge in nx.bfs_edges(G, s):
            nodes.add(edge[1])
            if len(nodes) == N:
                break
    return nx.subgraph(G, nodes)
    

def readRealGraph(filepath):
    edgesTS = []
    nodes = set()
    edges = set()
    lookup = {}
    c = 0
    G = nx.DiGraph()
    with open(filepath,'r') as fd:
        for line in fd.readlines():

            line = line.strip()
            items = line.split(' ')
            tstamp = ' '.join(items[0:2])
            tstamp = tstamp[1:-1]
            tstamp = datetime.strptime(tstamp, '%Y-%m-%d %H:%M:%S')
            t = items[2:4]
            t = map(int,t)
            if t[0] == t[1]:
                continue
            #t.sort(); #undirected

            if tuple(t) in lookup.keys():
                num = lookup[tuple(t)]
            else:
                num = c
                lookup[tuple(t)] = c
                c += 1
            edgesTS.append((tstamp, tuple(t), num ))
            nodes.add(t[0])
            nodes.add(t[1])
            edges.add(tuple([t[0],t[1]]))
            G.add_edge(t[0],t[1])
    fd.close()
    G = nx.DiGraph(G)
    G.remove_edges_from(G.selfloop_edges())
    return edgesTS, G, nodes, edges

def readFile(filepath, mode = 'general'):
    G = nx.DiGraph()
    TS = []
    snapshots = {}
    infected_set = set()
    recovered_set = set()
    reported_infection = set()
    reported_recovery = set()
    snapshoted_set = {'infected': set(), 'recovered': set()}

    #snapshoted_set = set()
    #infected = set()
    infection_order = []
    infection_track = {}
    seeds = set()
    with open(filepath,'r') as fd:
        for line in fd.readlines():
            if line[0] == '#':
                break
            line = line.strip()
            items = line.split('\t')
            tstamp = datetime.strptime(items[0], '%Y-%m-%d %H:%M:%S')
            ##print items
            #record = map(int, items[1:])
            if mode == 'grid':
                n1, n2 = literal_eval(items[1]), literal_eval(items[2])
            else:
                n1, n2 = int(items[1]), int(items[2])
            ##print n1, n2
            #exit()
            info = list(map(int, items[3:]))

            #record = items[1:3]+ map(int, items[3:])
            if n1 == n2:
                continue
            TS.append([tstamp] + [n1, n2]+ info)

            if info[0] == 1 and info[1] == 1 and n2 not in infected_set:
                infection_order.append((n1, n2))
                infection_track[n1] = infection_track.get(n1, []) + [n2]

            G.add_edge(n1, n2)
            if info[-2] == 1:
                seeds.add(n1)
            if info[-1] == 1:
                seeds.add(n2)

            if info[0] == 1:
                infected_set.add(n1)
            elif info[0] == -1:
                recovered_set.add(n1)
                if n1 in infected_set:
                    infected_set.remove(n1)
            if info[1] == 1:
                infected_set.add(n2)
            elif info[1] == -1:
                recovered_set.add(n2)
                if n2 in infected_set:
                    infected_set.remove(n2)

            if snapshoted_set['infected'] ^ infected_set or snapshoted_set['recovered'] ^ recovered_set:
                snapshoted_set['infected'] = copy.deepcopy(infected_set)
                snapshoted_set['recovered'] = copy.deepcopy(recovered_set)
                snapshots[tstamp] = copy.deepcopy(snapshoted_set)

    fd.close()
    return TS, G, infection_order, infection_track, seeds
	
def get_reachability(edgesTS, G):
    from_to = {}
    to_from = {}
    nodes = G.nodes()
    print ('total_nodes', len(nodes))
    for i in edgesTS:
        n1, n2 = i[1][0], i[1][1]
        if n1 in nodes and n2 in nodes:
            from_to.setdefault(n1, set())
            from_to[n1].add(n2)

            to_from.setdefault(n2, set())
            to_from[n2].add(n1)
            to_from.setdefault(n1, set())
            for j in to_from[n1]:
                from_to[j].add(n2)
                to_from[n2].add(j)

    return from_to

def generateTS_final(n = 100,
               p = 0.5,
               seed = 1.0,
               st = datetime(2000, 1, 1, 0, 0, 0),
               m = 1000,
               srcN = 10,
               reportingP = 0.1,
               infectionP = 0.7,
               recoveringP = 0.1,
               type = 'ER',
               real = False,
               name = 'generated',
               percentage = 0.5):
    #f = open(name + '.txt', 'w')
    if type == 'ER':
        G = nx.erdos_renyi_graph(n, p, seed, True)
    elif type == 'PL':
        G = nx.scale_free_graph(n, seed=seed)
    elif type == 'BA':
        G = nx.barabasi_albert_graph(n, 3, seed=None)
    elif type == 'grid':
        G = nx.grid_2d_graph(int(np.ceil(np.sqrt(n))), int(np.ceil(np.sqrt(n))))
    elif type in ['facebook', 'enron', 'twitter', 'students', 'tumblr']:
        print ('start reading')
        edgesTS, G, _, _ = readRealGraph(os.path.join("..", "..", "Data", type+".txt"))
        print ('end reading')
        #G = getGraph(edgesTS)
        #print 'got a graph'
        to_be_seeds = set()
        if G.number_of_nodes() > n:
            from_to = get_reachability(edgesTS, G)
            reachability = sorted([(len(v),k) for k, v in from_to.items()], reverse = True)
            if reachability[0][0] > n:
                chosen_seed = 0
                for idx in reachability:
                    if idx[0] < n:
                        break
                    else:
                        chosen_seed = idx[1]
                chosen = from_to[chosen_seed]
                chosen.add(chosen_seed)
                to_be_seeds.add(chosen_seed)
            else:
                chosen = set()
                for idx in reachability:
                    chosen.update(from_to[idx[1]])
                    chosen.add(idx[1])
                    to_be_seeds.add(idx[1])
                    if len(chosen) > n:
                        break

            G = nx.subgraph(G, chosen)
        print ('number of chosen nodes:', G.number_of_nodes())
        print ('number of seeds chosen:', len(to_be_seeds))
    #nx.draw(G)
    #plt.show()
    edges = G.edges()
    n = G.number_of_nodes()
    if type in ['ER', 'PL', 'BA', 'grid']:
        real = False
    if real:
        edgesTS = [i for i in edgesTS if i[1][0] in G.nodes() and i[1][1] in G.nodes()]

    print ('length of TS', len(edgesTS))
    m = len(edgesTS)
    externals = 0
    #infections = set(np.random.choice(range(1, m), size = srcN-1, replace = False))
    infections = set(np.random.choice(range(1, m), size = srcN, replace = False))
    print (infections)
    #infections.add(0)
    infected_set = set()
    recovered_set = set()
    recovered = 0


    TS = []
    snapshots = {}
    reported_infection = set()
    reported_recovery = set()
    snapshoted_set = {'infected': set(), 'recovered': set()}
    terminals = []
    i = -1
    #for i in xrange(m):
    infection_order = []
    infection_track = {}
    seeds = []
    fail = False
    while True:
        i += 1
        reported, external = 0, 0
        n1Inf, n2Inf = 0, 0
        if real == False:
            t = st + timedelta(seconds = i)
            e = edges[np.random.randint(0, G.number_of_edges())]

        if real == True:
            j = i % len(edgesTS)
            e = edgesTS[j][1]
            jt = i // len(edgesTS)
            dT = edgesTS[-1][0] - edgesTS[0][0]
            t = edgesTS[j][0] + jt*dT
        if i in infections:
            if e[0] not in infected_set and e[0] not in recovered_set:#to be externally infected
                infected_set.add(e[0])
                externals += 1
                seeds.append(e[0])
            elif e[1] not in infected_set and e[1] not in recovered_set:
                infected_set.add(e[1])
                externals += 1
                seeds.append(e[1])
            else:
                infections.add(i+1)

        if external != 1 and e[0] in infected_set: #not externally infected, try to cure
            if np.random.rand() < recoveringP:
                recovered_set.add(e[0])
                infected_set.remove(e[0])


        if e[0] in infected_set:
            n1Inf = 1
        elif e[0] in recovered_set:
            n1Inf = -1

        if snapshoted_set['infected'] ^ infected_set or snapshoted_set['recovered'] ^ recovered_set:
            snapshoted_set['infected'] = copy.deepcopy(infected_set)
            snapshoted_set['recovered'] = copy.deepcopy(recovered_set)
            snapshots[t] = copy.deepcopy(snapshoted_set)

        if e[1] in infected_set:
            n2Inf = 1
        elif e[1] in recovered_set:
            n2Inf = -1
        else:
            if n1Inf == 1:
                if np.random.rand() < infectionP:
                    infected_set.add(e[1])
                    n2Inf = 1
                    infection_order.append(e)
                    infection_track[e[0]] = infection_track.get(e[0], []) + [e[1]]

        TS.append([t, e[0], e[1], n1Inf, n2Inf, 0, 0, 0, 0])
        #f.write('\t'.join([str(t), str(e[0]), str(e[1]), str(n1Inf), str(n2Inf), str(reported), str(external)])+'\n')
        print (len(infected_set), i, externals)
        if len(infected_set) > n*percentage:
            print ('enough is infected', len(infected_set))
            if externals == srcN:
                print ('sources are generated')
                break
        if i > 100000:
            fail = True
            break
    #f.close()
    print ('length of generated', len(TS), len(infected_set), G.number_of_nodes(), externals)
    return TS, G, infection_order, infection_track, seeds, fail
    #return TS, snapshots, G	


if __name__ == "__main__":
    TS, G, infection_order, infection_track, seeds, nodes =  get_noisy_TS_equiprob(type='BA', n = 100, inf_fraction = 0.5, seeds_num = 3, noise = 100)