def call(Map config) {

    def threshold = config.threshold ?: 80

    def coverage = sh(
        script: """
        python3 - <<EOF
import xml.etree.ElementTree as ET
tree = ET.parse('coverage.xml')
root = tree.getroot()
rate = float(root.attrib['line-rate']) * 100
print(int(rate))
EOF
        """,
        returnStdout: true
    ).trim().toInteger()

    echo "Measured coverage: ${coverage}%"

    if (coverage < threshold) {
        error "❌ Coverage ${coverage}% is below threshold ${threshold}%"
    }

    echo "✅ Coverage gate passed"
}
