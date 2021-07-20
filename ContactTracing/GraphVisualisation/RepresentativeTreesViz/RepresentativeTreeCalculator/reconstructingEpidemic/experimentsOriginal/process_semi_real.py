import os
import sys
import uuid

from utils.generator_noise import *
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
type = 'facebook'
type = 'twitter'
type = 'students'
type = 'tumblr'
type = 'enron'
p = 4.0/N
real = False
plt.rcParams.update({'font.size': 15, 'lines.linewidth': 3})

path = os.path.join('CONDOR', 'semi_real', 'res')
f = [f for f in listdir(path) if isfile(join(path, f)) and type in f][0]
fullname = os.path.join(path, f)
print fullname
arr_abs_values_tp, arr_abs_values_p, arr_abs_values_lb_tp, arr_abs_gt_positive, arr_abs_values_ub_tp, arr_abs_values_ub_p,\
arr_prec_infected, arr_prec_lb_infected, arr_prec_ub_infected,\
arr_recall_infected, arr_recall_lb_infected, arr_recall_ub_infected,\
arr_MCC, arr_MCC_lb, arr_MCC_ub,\
arr_F1, arr_F1_lb, arr_F1_ub = pickle.load(open(fullname, "rb"))

title = 'absolute values'
plt.figure('absolute values')
num = 102

plt.plot(np.nanmean([i for i in arr_abs_values_tp if not isinstance(i, float) and len(i) == num], axis=0), 'k-')
plt.plot(np.nanmean([i for i in arr_abs_values_p if not isinstance(i, float) and len(i) == num], axis=0), 'k:')
plt.plot(np.nanmean([i for i in arr_abs_values_lb_tp if not isinstance(i, float) and len(i) == num], axis=0), 'r--')
plt.plot(np.nanmean([i for i in arr_abs_gt_positive if not isinstance(i, float) and len(i) == num], axis=0), 'r-')
plt.plot(np.nanmean([i for i in arr_abs_values_ub_tp if not isinstance(i, float) and len(i) == num], axis=0), 'b-')
plt.plot(np.nanmean([i for i in arr_abs_values_ub_p if not isinstance(i, float) and len(i) == num], axis=0), 'b:')

plt.xlabel('snapshots')
plt.legend(['TP output', 'output', 'reports', 'GT', 'TP BL', 'output BL'], loc = 3)
##plt.title(title)
name = type + '_positive'+'.pdf'
plt.tight_layout()
name = plt.savefig(name)


title = 'accuracy'
plt.figure('accuracy')
plt.plot(np.nanmean([i for i in arr_prec_infected if not isinstance(i, float) and len(i) == num], axis=0), 'k-')
plt.plot(np.nanmean([i for i in arr_prec_lb_infected if not isinstance(i, float) and len(i) == num], axis=0), 'k--')
plt.plot(np.nanmean([i for i in arr_prec_ub_infected if not isinstance(i, float) and len(i) == num], axis=0), 'k:')
plt.plot(np.nanmean([i for i in arr_recall_infected if not isinstance(i, float) and len(i) == num], axis=0),'r-')
plt.plot(np.nanmean([i for i in arr_recall_lb_infected if not isinstance(i, float) and len(i) == num], axis=0), 'r--')
plt.plot(np.nanmean([i for i in arr_recall_ub_infected if not isinstance(i, float) and len(i) == num], axis=0), 'r:')
plt.xlabel('snapshots')
plt.ylim(ymax = 1.01, ymin = -0.1)
plt.legend(['Prec', 'Prec reports', 'Prec BL', 'Recall', 'Recall reports', 'Recall BL'], loc = 3)
#plt.title(title)
name = type + '_acc'+'.pdf'
plt.tight_layout()
name = plt.savefig(name)


title = 'MCC'
plt.figure('MCC')
plt.plot(np.nanmean([i for i in arr_MCC if not isinstance(i, float) and len(i) == num], axis=0), 'k-')
plt.plot(np.nanmean([i for i in arr_MCC_lb if not isinstance(i, float) and len(i) == num], axis=0), 'k--')
plt.plot(np.nanmean([i for i in arr_MCC_ub if not isinstance(i, float) and len(i) == num], axis=0), 'k:')

plt.xlabel('snapshots')
plt.ylim(ymax = 1.01, ymin = -0.1)
plt.legend(['MCC', 'MCC reports', 'MCC BL'], loc = 3)
#plt.title(title)
name = type + '_mcc'+'.pdf'
plt.tight_layout()
name = plt.savefig(name)

title = 'F1'
plt.figure('F1')
plt.plot(np.nanmean([i for i in arr_F1 if not isinstance(i, float) and len(i) == num], axis=0), 'k-')
plt.plot(np.nanmean([i for i in arr_F1_lb if not isinstance(i, float) and len(i) == num], axis=0), 'k--')
plt.plot(np.nanmean([i for i in arr_F1_ub if not isinstance(i, float) and len(i) == num], axis=0), 'k:')
plt.xlabel('snapshots')
plt.ylim(ymax = 1.01, ymin = -0.1)
plt.legend(['F1', 'F1 reports', 'F1 BL'], loc = 3)
#plt.title(title)
name = type + '_F1'+'.pdf'

plt.tight_layout()
name = plt.savefig(name)

#plt.show()
