#!/usr/bin/env python3
"""Reusable Python dependency compatibility testing library"""

import pkg_resources
import sys
import os

def test_package_requirements(package_name):
    """Test that a package's requirements are satisfied"""
    try:
        # Import the package
        __import__(package_name.replace('-', '_'))
        print(f"PASS: {package_name} import successful")
        
        # Get package distribution and requirements
        dist = pkg_resources.get_distribution(package_name)
        requirements = dist.requires()
        
        print(f"{package_name} version: {dist.version}")
        
        if not requirements:
            print(f"PASS: {package_name} has no dependencies to check")
            return True
        
        # Check each requirement
        for req in requirements:
            try:
                pkg_resources.require(str(req))
                print(f"PASS: {req} satisfied")
            except pkg_resources.DistributionNotFound:
                print(f"FAIL: {req} not found")
                return False
            except pkg_resources.VersionConflict as e:
                print(f"FAIL: {req} version conflict: {e}")
                return False
        
        return True
    except Exception as e:
        print(f"FAIL: {package_name} test failed: {e}")
        return False

def get_python_rdepends_from_env():
    """Extract Python dependencies from RDEPENDS environment variable"""
    rdepends = os.environ.get('RDEPENDS', '')
    python_deps = []
    
    for dep in rdepends.split():
        if dep.startswith('python3-') and not dep.endswith('-native'):
            pkg_name = dep.replace('python3-', '')
            python_deps.append(pkg_name)
    
    return python_deps

def test_integration_with(package_name, integration_packages):
    """Test integration with other packages"""
    success = True
    
    for integration_pkg in integration_packages:
        try:
            __import__(package_name.replace('-', '_'))
            __import__(integration_pkg.replace('-', '_'))
            print(f"PASS: {package_name} integrates with {integration_pkg}")
        except ImportError:
            print(f"SKIP: {integration_pkg} not available")
        except Exception as e:
            print(f"FAIL: {package_name}-{integration_pkg} integration failed: {e}")
            success = False
    
    return success

def main():
    """Main test function"""
    if len(sys.argv) < 2:
        print("FAIL: Package name required as argument")
        return 1
    
    package_name = sys.argv[1]
    print(f"=== Testing {package_name} dependency compatibility ===")
    
    success = True
    success &= test_package_requirements(package_name)
    
    # Get integration packages from RDEPENDS
    integration_packages = get_python_rdepends_from_env()
    if integration_packages:
        print(f"Found Python dependencies from RDEPENDS: {integration_packages}")
        success &= test_integration_with(package_name, integration_packages)
    
    # Always output final result
    if success:
        print(f"PASS: {package_name} dependency test completed successfully")
    else:
        print(f"FAIL: {package_name} dependency test failed")
    
    return 0 if success else 1

if __name__ == "__main__":
    sys.exit(main())
