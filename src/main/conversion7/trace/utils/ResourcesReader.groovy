package conversion7.trace.utils


class ResourcesReader {
    static String loadResource(final String fileName) {
        return ResourcesReader.getClassLoader().getResourceAsStream(fileName).text
    }

}
