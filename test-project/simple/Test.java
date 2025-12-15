public class Test {
    @SuppressWarnings("unused")
    void suppressUnusedWarning() {
        int usedVal = 5;
        int unusedVal = 10;  // no warning here
        List<Integer> list = new ArrayList<>();
        list.add(usedVal);
    }
}
