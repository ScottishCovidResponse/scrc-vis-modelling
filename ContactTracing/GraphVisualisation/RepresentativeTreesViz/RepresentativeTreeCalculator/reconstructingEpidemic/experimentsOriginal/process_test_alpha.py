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

model = 'SI'
notmodel = 'SI_1'
#notmodel = 'q'
folder = ''
#plt.rcParams.update({'font.size': 15, 'lines.linewidth': 3})
plt.rcParams.update({'font.size': 20, 'lines.linewidth': 3})
#path = os.path.join('CONDOR', 'test_alpha', 'res_K1')
path = os.path.join('CONDOR', 'test_alpha', 'res_K5_smallerA')
f = [f for f in listdir(path) if isfile(join(path, f)) and model in f and notmodel not in f][0]
fullname = os.path.join(path, f)
arr_Ks, arr_alphas, arr_cost_RHT, arr_ratio = pickle.load(open(fullname, "rb"))
idx = 5
range_len = len(arr_alphas)
#print len(arr_Ks[0])
#arr_Ks, arr_alphas, arr_cost_RHT, arr_ratio = arr_Ks[idx][0:range_len], arr_alphas[idx][0:range_len], arr_cost_RHT[idx][0:range_len], arr_ratio[idx][0:range_len]

title = 'alpha vs K'
plt.figure('alpha vs K')
plt.plot(np.nanmean(arr_alphas, axis=0), np.nanmean(arr_Ks, axis=0))
plt.axhline(y=5,linewidth=2, color='k', linestyle = ':')
#plt.plot(arr_alphas, arr_Ks)
plt.xlabel('alpha', fontsize=27)
plt.ylabel('k', fontsize=27)
plt.yticks(list(plt.yticks()[0]) + [5.0])
name = model+'_alpha_vs_K' + '_'+ str(type)+'.pdf'
#name = model+'_alpha_vs_K' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
#name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)

title = 'K vs tree cost'
plt.figure('K vs tree cost')
plt.plot(np.nanmean(arr_Ks, axis=0), np.nanmean(arr_cost_RHT, axis=0))
plt.axvline(x=5, linewidth=2, color='k', linestyle = ':')
#plt.plot(arr_Ks, arr_cost_RHT)
plt.xlabel('k', fontsize=27)
plt.ylabel('tree cost', fontsize=27)
plt.xticks(list(plt.xticks()[0]) + [5.0])
name = model+'_K_vs_tree' + '_'+ str(type)+'.pdf'
#name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)
exit()

title = 'K vs tree cost by alpha'
plt.figure('K vs tree cost by alpha')
plt.plot(np.nanmean(arr_Ks, axis=0), [np.nanmean(arr_cost_RHT, axis=0)[i]/np.nanmean(arr_Ks, axis=0)[i] for i in xrange(len(np.nanmean(arr_cost_RHT, axis=0)))])
#plt.plot(arr_Ks, [arr_cost_RHT[i]/arr_Ks[i] for i in xrange(len(arr_cost_RHT))])
plt.xlabel('K')
plt.ylabel('tree cost div by alpha')
name = model+'K_vs_tree_cost_div_by_alpha' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
name = plt.savefig(name)
plt.show()
exit()


title = 'alpha vs tree cost'
plt.figure('alpha vs tree cost')
plt.plot(np.nanmean(arr_alphas, axis=0),np.nanmean(arr_cost_RHT, axis=0))
plt.xlabel('alpha')
plt.ylabel('tree cost')
name = model+'alpha_vs_tree_cost' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
name = plt.savefig(name)

title = 'alpha vs tree cost div by K'
plt.figure('alpha vs tree cost/K')
plt.plot(np.nanmean(arr_alphas, axis=0),np.nanmean(arr_ratio, axis=0))
plt.xlabel('alpha')
plt.ylabel('tree cost/K')
name = model+'alpha_vs_tree_cost_div_by_K' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
name = name+'_'+time.strftime("%Y%m%d-%H%M%S")+'.pdf'
name = os.path.join(folder, name)
name = plt.savefig(name)

plt.show()
