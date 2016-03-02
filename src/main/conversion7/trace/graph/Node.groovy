package conversion7.trace.graph

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true,
        includes = ["name", "children"]
)
@EqualsAndHashCode()
class Node {
    String name

    List<Node> children
    Integer size = 2000

    transient Node parent

    Node(String name) {
        this.name = name
    }

    void addChildren(Node node) {
        getChildren().add(node)
        node.parent = this
    }

    List<Node> getChildren() {
        if (this.@children == null) {
            children = new ArrayList();
        }
        return children
    }

}
