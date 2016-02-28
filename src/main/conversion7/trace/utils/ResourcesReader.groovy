package conversion7.trace.utils


class ResourcesReader {
    static String loadResource(final String fileName) {
        return ResourcesReader.getClassLoader().getResourceAsStream(fileName).text
    }

    static File findResource(final String relativeFilePath) {
        return new File(ResourcesReader.getClassLoader().getResource(relativeFilePath).toURI())
    }
}
