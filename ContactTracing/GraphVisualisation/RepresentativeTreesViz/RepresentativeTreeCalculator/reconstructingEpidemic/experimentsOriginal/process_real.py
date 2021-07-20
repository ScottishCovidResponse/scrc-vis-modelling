import os
import sys
import uuid

#from utils.generator import *
from utils.generator_noise import *
from utils.generator_noise_misc import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.visualization import *
from utils.paths import *
from utils.postprocessing import *
from utils.get_path_stats import *
import pickle

M = 30
N = 100
K = 5
infectionP = 0.3
reportingP = 0.5
recoveringP = 0.0
#K = srcN
#dataset = str(sys.argv[1])
#type = 'ER'
#dataset = 'flixter_1000_K9'
dataset = 'flixter_1000_K9'
#dataset = 'flixter_K5.txt_0-5'
#dataset = 'flixter_K5.txt_0-5'
plt.rcParams.update({'font.size': 25, 'lines.linewidth': 3})
plt.rcParams['xtick.labelsize'] = 25
plt.rcParams['ytick.labelsize'] = 25

path = os.path.join('CONDOR', 'real', 'res_flixter')
#path='.'
print path
onlyfiles = [f for f in listdir(path) if isfile(join(path, f)) and f[0:len(dataset)] == dataset]
print onlyfiles
max_iter = len(onlyfiles)

arr_abs_values_tp = [0] * max_iter
arr_abs_values_p = [0] * max_iter
arr_abs_values_lb_tp = [0] * max_iter
arr_abs_gt_positive = [0] * max_iter
arr_abs_values_ub_tp = [0] * max_iter
arr_abs_values_ub_p = [0] * max_iter

arr_prec_infected = [0] * max_iter
arr_prec_lb_infected = [0] * max_iter
arr_prec_ub_infected = [0] * max_iter

arr_recall_infected = [0] * max_iter
arr_recall_lb_infected = [0] * max_iter
arr_recall_ub_infected = [0] * max_iter

arr_MCC = [0] * max_iter
arr_MCC_lb = [0] * max_iter
arr_MCC_ub = [0] * max_iter

arr_F1 = [0] * max_iter
arr_F1_lb = [0] * max_iter
arr_F1_ub = [0] * max_iter


iter = 0

for f in onlyfiles:
    fullname = os.path.join(path, f)
    #print fullname
    abs_values_tp, abs_values_p, abs_values_lb_tp, abs_gt_positive, abs_values_ub_tp, abs_values_ub_p,\
             prec_infected, prec_lb_infected, prec_ub_infected,\
             recall_infected, recall_lb_infected, recall_ub_infected,\
             MCC, MCC_lb, MCC_ub,\
             F1, F1_lb, F1_ub = pickle.load(open(fullname, "rb"))

    arr_abs_values_tp[iter] = abs_values_tp
    arr_abs_values_p[iter] = abs_values_p
    arr_abs_values_lb_tp[iter] = abs_values_lb_tp
    arr_abs_gt_positive[iter] = abs_gt_positive
    arr_abs_values_ub_tp[iter] = abs_values_ub_tp
    arr_abs_values_ub_p[iter] = abs_values_ub_p

    arr_prec_infected[iter] = prec_infected
    arr_prec_lb_infected[iter] = prec_lb_infected
    arr_prec_ub_infected[iter] = prec_ub_infected

    arr_recall_infected[iter] = recall_infected
    arr_recall_lb_infected[iter] = recall_lb_infected
    arr_recall_ub_infected[iter] = recall_ub_infected

    arr_MCC[iter] = MCC
    arr_MCC_lb[iter] = MCC_lb
    arr_MCC_ub[iter] = MCC_ub

    arr_F1[iter] = F1
    arr_F1_lb[iter] = F1_lb
    arr_F1_ub[iter] = F1_ub

    iter += 1

title = 'absolute values'
plt.figure('absolute values')
plt.plot(np.nanmean(arr_abs_values_tp, axis=0), 'k-')
plt.plot(np.nanmean(arr_abs_values_p, axis=0), 'k:')
plt.plot(np.nanmean(arr_abs_values_lb_tp, axis=0), 'r--')
plt.plot(np.nanmean(arr_abs_gt_positive, axis=0), 'r-')
plt.plot(np.nanmean(arr_abs_values_ub_tp, axis=0), 'b-')
plt.plot(np.nanmean(arr_abs_values_ub_p, axis=0), 'b:')

plt.xlabel('snapshots')
plt.legend(['TP output', 'output', 'reports', 'GT', 'TP BL', 'output BL'], loc = 3)
#plt.title(title)
name = dataset + '_positive'+'.pdf'
plt.tight_layout()
name = plt.savefig(name)


title = 'accuracy'
plt.figure('accuracy')
plt.plot(np.nanmean(arr_prec_infected, axis=0), 'k-')
plt.plot(np.nanmean(arr_prec_lb_infected, axis=0), 'k--')
plt.plot(np.nanmean(arr_prec_ub_infected, axis=0), 'k:')
plt.plot(np.nanmean(arr_recall_infected, axis=0),'r-')
plt.plot(np.nanmean(arr_recall_lb_infected, axis=0), 'r--')
plt.plot(np.nanmean(arr_recall_ub_infected, axis=0), 'r:')
plt.xlabel('snapshots')
plt.ylim(ymax = 1.01, ymin = -0.1)
plt.legend(['Prec', 'Prec reports', 'Prec BL', 'Recall', 'Recall reports', 'Recall BL'], loc = 3)
#plt.title(title)
name = dataset + '_acc'+'.pdf'
plt.tight_layout()
name = plt.savefig(name)


title = 'MCC'
plt.figure('MCC')
plt.plot(np.nanmean(arr_MCC, axis=0))
plt.plot(np.nanmean(arr_MCC_lb, axis=0))
plt.plot(np.nanmean(arr_MCC_ub, axis=0))

plt.xlabel('snapshots')
plt.ylim(ymax = 1.0, ymin = 0.0)
#plt.legend(['CulT', 'reports', 'baseline'], loc = 3, fontsize=30)
#plt.title(title)
name = dataset + '_mcc'+'.pdf'
plt.tight_layout()
name = plt.savefig(name)

title = 'F1'
plt.figure('F1')
plt.plot(np.nanmean(arr_F1, axis=0), 'k-')
plt.plot(np.nanmean(arr_F1_lb, axis=0), 'k--')
plt.plot(np.nanmean(arr_F1_ub, axis=0), 'k:')
plt.xlabel('snapshots')
plt.ylim(ymax = 1.0, ymin = 0.0)
plt.legend(['F1', 'F1 reports', 'F1 BL'], loc = 3)
#plt.title(title)
name = dataset + '_F1' + '.pdf'

plt.tight_layout()
name = plt.savefig(name)

#plt.show()
