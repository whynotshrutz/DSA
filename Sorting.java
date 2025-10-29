import java.util.*;
import java.util.function.IntUnaryOperator;

public class Sorting {

    /* ---------- Utilities ---------- */
    private static void swap(int[] a, int i, int j){ int t = a[i]; a[i] = a[j]; a[j] = t; }
    private static boolean isSorted(int[] a){ for(int i=1;i<a.length;i++) if(a[i] < a[i-1]) return false; return true; }

    /* ---------- O(n^2) ---------- */
    public static void insertionSort(int[] a){
        for (int i=1;i<a.length;i++){
            int key=a[i], j=i-1;
            while (j>=0 && a[j]>key){ a[j+1]=a[j]; j--; }
            a[j+1]=key;
        }
    }
    public static void selectionSort(int[] a){
        for (int i=0;i<a.length-1;i++){
            int m=i;
            for(int j=i+1;j<a.length;j++) if(a[j]<a[m]) m=j;
            swap(a,i,m);
        }
    }
    public static void bubbleSort(int[] a){
        boolean swapped=true;
        for(int n=a.length; swapped && n>1; n--){
            swapped=false;
            for(int i=1;i<n;i++){
                if(a[i-1]>a[i]){ swap(a,i-1,i); swapped=true; }
            }
        }
    }

    /* ---------- Merge Sort (stable) ---------- */
    public static void mergeSort(int[] a){ mergeSort(a,0,a.length-1,new int[a.length]); }
    private static void mergeSort(int[] a,int l,int r,int[] buf){
        if(l>=r) return;
        int m=(l+r)>>>1;
        mergeSort(a,l,m,buf); mergeSort(a,m+1,r,buf);
        int i=l,j=m+1,k=l;
        while(i<=m && j<=r) buf[k++]= (a[i]<=a[j])? a[i++]:a[j++];
        while(i<=m) buf[k++]=a[i++]; while(j<=r) buf[k++]=a[j++];
        for(i=l;i<=r;i++) a[i]=buf[i];
    }

    /* ---------- Quick Sort (3-way, tail-rec) ---------- */
    public static void quickSort(int[] a){ quick(a,0,a.length-1,new Random(42)); }
    private static void quick(int[] a,int l,int r, Random rng){
        while(l<r){
            int p=l+r>>>1;
            int pivot = a[p];
            int i=l, lt=l, gt=r;
            while(i<=gt){
                if(a[i]<pivot) swap(a,lt++,i++);
                else if(a[i]>pivot) swap(a,i,gt--);
                else i++;
            }
            // Recurse into smaller side first (tail recursion)
            if(lt-l < r-gt){ quick(a,l,lt-1,rng); l=gt+1; }
            else { quick(a,gt+1,r,rng); r=lt-1; }
        }
    }

    /* ---------- Heap Sort ---------- */
    public static void heapSort(int[] a){
        int n=a.length;
        for(int i=n/2-1;i>=0;i--) heapify(a,n,i);
        for(int end=n-1; end>0; end--){
            swap(a,0,end);
            siftDown(a,0,end);
        }
    }
    private static void heapify(int[] a,int n,int i){ siftDown(a,i,n); }
    private static void siftDown(int[] a,int i,int n){
        for(;;){
            int l=2*i+1, r=l+1, m=i;
            if(l<n && a[l]>a[m]) m=l;
            if(r<n && a[r]>a[m]) m=r;
            if(m==i) break;
            swap(a,i,m); i=m;
        }
    }

    /* ---------- Counting Sort (non-negative small ints) ---------- */
    public static void countingSort(int[] a){
        if(a.length==0) return;
        int max=0; for(int v: a) if(v>max) max=v;
        int[] c=new int[max+1];
        for(int v: a) c[v]++;
        int i=0;
        for(int v=0; v<c.length; v++) while(c[v]-- > 0) a[i++]=v;
    }

    /* ---------- Radix Sort (LSD base 256) ---------- */
    public static void radixSortNonNegative(int[] a){
        int n=a.length; if(n==0) return;
        int[] out=new int[n];
        for(int shift=0; shift<32; shift+=8){
            int[] cnt=new int[256];
            for(int v: a) cnt[(v>>>shift)&255]++;
            int sum=0; for(int i=0;i<256;i++){ int t=cnt[i]; cnt[i]=sum; sum+=t; }
            for(int v: a) out[cnt[(v>>>shift)&255]++]=v;
            System.arraycopy(out,0,a,0,n);
        }
    }

    /* ---------- Shell Sort (Ciura-ish gaps) ---------- */
    public static void shellSort(int[] a){
        int[] gaps = {701,301,132,57,23,10,4,1};
        for(int g: gaps){
            for(int i=g;i<a.length;i++){
                int t=a[i], j=i;
                while(j>=g && a[j-g]>t){ a[j]=a[j-g]; j-=g; }
                a[j]=t;
            }
        }
    }

    /* ---------- CLI ---------- */
    private static Map<String, java.util.function.Consumer<int[]>> algos(){
        Map<String, java.util.function.Consumer<int[]>> m = new LinkedHashMap<>();
        m.put("insertion", Sorting::insertionSort);
        m.put("selection", Sorting::selectionSort);
        m.put("bubble", Sorting::bubbleSort);
        m.put("merge", Sorting::mergeSort);
        m.put("quick", Sorting::quickSort);
        m.put("heap", Sorting::heapSort);
        m.put("counting", Sorting::countingSort);
        m.put("radix", Sorting::radixSortNonNegative);
        m.put("shell", Sorting::shellSort);
        return m;
    }

    public static void main(String[] args){
        String algo = args.length>0 ? args[0].toLowerCase() : "quick";
        int n = args.length>1 ? Integer.parseInt(args[1]) : 100000;
        int seed = args.length>2 ? Integer.parseInt(args[2]) : 7;

        int[] a = new Random(seed).ints(n, 0, Math.max(10, n)).toArray();

        var m = algos();
        if(!m.containsKey(algo)){
            System.out.println("Algos: "+m.keySet());
            return;
        }
        long t0=System.nanoTime();
        m.get(algo).accept(a);
        long t1=System.nanoTime();

        System.out.printf(Locale.ROOT, "algo=%s n=%d millis=%.3f sorted=%s%n",
                algo, n, (t1-t0)/1e6, isSorted(a));
    }
}
