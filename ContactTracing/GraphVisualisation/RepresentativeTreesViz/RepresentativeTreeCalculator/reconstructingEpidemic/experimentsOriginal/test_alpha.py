import os
import sys
import uuid

#from utils.generator import *
from utils.generator_noise import *
from utils.generator_noise_misc import generateTS_final
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.visualization import *
from utils.paths import *
from utils.postprocessing import *
from utils.get_path_stats import *
import pickle

M = 30
N = 100
srcN = 5
infectionP = 0.3
reportingP = 0.5
recoveringP = 0.0
K = srcN

#type = 'PL'
#type = str(sys.argv[1])
# type = 'facebook'
# type = 'twitter'
# type = 'students'
# type = 'tumblr'
# type = 'enron'
p = 4.0/N
real = False

type = 'facebook'
#model = str(sys.argv[2])

#TS, snapshots, G = readFile('generated.txt')

I = 10
arr_Ks = [0] * I
arr_alphas = [0] * I
arr_cost_RHT = [0] * I
arr_ratio = [0] * I

noise = 100
dt_sec = 100
#noise = 1
#dt_sec = 1
model = 'SI_1'
for iters in xrange(I):

    TS, G, infection_order, infection_track, seeds, nodes = get_noisy_TS_empty_reports(type = type, n = N, inf_fraction = 0.5, seeds_num = srcN, noise = noise, pad = dt_sec, model = model)
    e = np.linalg.eigvals(nx.to_numpy_matrix(G))
    #print("Largest eigenvalue:", max(e), 1.0/max(e))
    #exit()
    print seeds
    K = len(seeds)
    #K = 3
    #H =  get_infection_tree(TS)
    #sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources(TS, mode = 'all')
    sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources_shifted(TS, G = nx.Graph(), mode = 'all', dt_sec = dt_sec, rep_prob = reportingP)
    print len(sinks), len(sources), len(reported['infected'])
    #pickle.dump([TS, sources, sinks, immuned, unreported], open("save.p", "wb"))
    #TS, sources, sinks, immuned, unreported = pickle.load( open( "save.p", "rb" ) )
    #exit()
    #SP = shortestPath1(TS, sources, sinks, immuned, unreported)
    SP = shortestPath1(TS, sources, sinks, immuned, unreported)
    #K = srcN

    print 'K =', K
    #cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)

    Ks, alphas, cost_RHT = greedy_plot_KvsAlpha(SP, len(sinks), TS, ticks = 100, ua = 0.25)

    arr_Ks[iters] = Ks
    arr_alphas[iters] = alphas
    arr_cost_RHT[iters] = cost_RHT
    arr_ratio[iters] = [cost_RHT[i]/Ks[i] for i in xrange(len(cost_RHT))]

unique_out = type + '_' + model + '_'+ str(uuid.uuid4())
pickle.dump([arr_Ks, arr_alphas, arr_cost_RHT, arr_ratio], open(unique_out+".p", "wb"))
#exit()
folder = ''

title = 'alpha vs K'
plt.figure('alpha vs K')
plt.plot(np.nanmean(arr_alphas, axis=0),np.nanmean(arr_Ks, axis=0))
plt.xlabel('alpha')
plt.ylabel('K')
name = 'alpha_vs_K' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
name = plt.savefig(name)


title = 'alpha vs tree cost'
plt.figure('alpha vs tree cost')
plt.plot(np.nanmean(arr_alphas, axis=0),np.nanmean(arr_ratio, axis=0))
plt.xlabel('alpha')
plt.ylabel('tree cost')
name = 'alpha_vs_tree_cost' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
name = plt.savefig(name)

title = 'alpha vs tree cost div by K'
plt.figure('alpha vs tree cost/K')
plt.plot(np.nanmean(arr_alphas, axis=0),np.nanmean(arr_cost_RHT, axis=0))
plt.xlabel('alpha')
plt.ylabel('tree cost/K')
name = 'alpha_vs_tree_cost' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
name = plt.savefig(name)

plt.show()
