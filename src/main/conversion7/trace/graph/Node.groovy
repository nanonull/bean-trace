package conversion7.trace.graph

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
@EqualsAndHashCode()
class Node {
    String name

    List<Node> getChildren() {
        if (this.@children == null) {
            children = new ArrayList();
        }
        return children
    }
    List<Node> children
    Integer size = 2000

    Node(String name) {
        this.name = name
    }

    def addChildren(Node node) {
        getChildren().add(node)
    }
}
