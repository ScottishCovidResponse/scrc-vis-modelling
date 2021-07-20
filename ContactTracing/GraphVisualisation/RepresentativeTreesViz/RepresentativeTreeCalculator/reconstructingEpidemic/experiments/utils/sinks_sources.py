from datetime import datetime, timedelta
import networkx as nx
from utils.accuracy import *



def get_sinks_and_sources_shifted(TS, G = nx.Graph(), mode = 'all', dt_sec = 1, rep_prob = 1.0, to_be_reported = []):
    immuned, sinks, sources, reported, unreported = {}, {}, {}, {}, {}
    sinks_TnI, sources_TnI, unreported_TnI = {}, {}, {}
    reported['infected'] = set()
    reported['recovered'] = set()
    default, default_itr = TS[-1][0], len(TS)-1
    dt = timedelta(seconds = dt_sec)
    max_t = TS[-1][0]

    nodes = set()
    infected = set()
    for iter in range(len(TS)):
        interaction = TS[iter]
        t, n1, n2, status1, status2, report1, report2 = interaction[0], interaction[1], interaction[2], interaction[3], interaction[4], interaction[5], interaction[6]
        nodes.add(n1)
        nodes.add(n2)
        if status1 == 1 and n1 not in infected:
            infected.add(n1)
            if (not to_be_reported or n1 in to_be_reported) and np.random.rand() <= rep_prob:
                shift = np.random.randint(0, dt_sec+1)
                shifted_it = min(iter + shift, len(TS)-1)
                shifted_t = TS[shifted_it][0]
                #shifted_t = min(t + timedelta(seconds = shift), max_t)
                sinks[n1] = shifted_t
                #shifted_it = min(iter + shift, len(TS)-1)
                sinks_TnI[n1] = (shifted_t, shifted_it)

                sources[n1] = shifted_t
                sources_TnI[n1] = (shifted_t, shifted_it)

                reported['infected'].add(n1)

        if status2 == 1 and n2 not in infected:
            infected.add(n2)
            if (not to_be_reported or n2 in to_be_reported) and np.random.rand() <= rep_prob:
                shift = np.random.randint(0, dt_sec+1)
                shifted_it = min(iter + shift, len(TS)-1)
                shifted_t = TS[shifted_it][0]
                #shifted_t = min(t + timedelta(seconds = shift), max_t)
                sinks[n2] = shifted_t
                #shifted_it = min(iter + shift, len(TS)-1)
                sinks_TnI[n2] = (shifted_t, shifted_it)

                sources[n2] = shifted_t
                sources_TnI[n2] = (shifted_t, shifted_it)

                reported['infected'].add(n2)

    for n in nodes:
        if n not in sources:
            unreported[n] = default
            unreported_TnI[n] = (default, default_itr)
            if mode == 'all':
                sources[n] = sources.get(n, default)

    if mode == 'neigh':
        for u in sources:
            for v in G.neighbors_iter(u):
                sources[v] = sources.get(v, default)

    return sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI

def get_sinks_and_sources(TS, G = nx.Graph(), mode = 'reported'):
    immuned, sinks, sources, reported, unreported = {}, {}, {}, {}, {}
    sinks_TnI, sources_TnI, unreported_TnI = {}, {}, {}
    reported['infected'] = set()
    reported['recovered'] = set()
    #delta = ((TS[-1][0] - TS[0][0]).total_seconds())/2
    #default = TS[0][0] + timedelta(seconds = delta)
    default, default_itr = TS[-1][0], len(TS)-1

    nodes = set()
    for iter in range(len(TS)):
        interaction = TS[iter]
        t, n1, n2, status1, report = interaction[0], interaction[1], interaction[2], interaction[3], interaction[5]
        nodes.add(n1)
        nodes.add(n2)
        if report == 1:
            if status1 == 1 and (n1 not in sources or sources[n1] == default):
                sources[n1] = t
                sources_TnI[n1] = (t, iter)
                sinks[n1] = t
                sinks_TnI[n1] = (t,iter)
                # if time_only:
                #     sinks[n1] = t
                # else:
                #     sinks[n1] = (t, iter)
                reported['infected'].add(n1)
            elif status1 == -1 and n1 not in immuned:
                immuned[n1] = t
                reported['recovered'].add(n1)
                if n1 not in sources:
                    sources[n1] = TS[iter-1][0]
                    sources_TnI[n1] = (TS[iter-1][0], iter-1)
                    sinks[n1] = TS[iter-1][0]
                    sinks_TnI[n1] = (TS[iter-1][0], iter-1)
                    # if time_only:
                    #     sinks[n1] = TS[iter-1][0]
                    # else:
                    #     sinks[n1] = (TS[iter-1][0], iter-1)
        #elif mode == 'all':
            #default = TS[-1][0]
        #    sources[n1] = sources.get(n1, default)

    #sources = infected
    for n in nodes:
        if n not in sources:
            unreported[n] = default
            unreported_TnI[n] = (default, default_itr)
            if mode == 'all':
                sources[n] = sources.get(n, default)

    if mode == 'neigh':
        for u in sources:
            for v in G.neighbors_iter(u):
                sources[v] = sources.get(v, default)
    #sinks = infected

    return sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI
