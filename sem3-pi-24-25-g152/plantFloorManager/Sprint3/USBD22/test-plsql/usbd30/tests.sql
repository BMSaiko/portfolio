-- -------------------------------------------------------------------
-- Test Suite for deduct_stock Function
-- -------------------------------------------------------------------
SET SERVEROUTPUT ON SIZE 1000000;

DECLARE
    -- Variables to hold function outputs
    v_result VARCHAR2(4000);
    
    -- Custom exception for test failures
    e_test_failure EXCEPTION;
    
    -- Helper procedure to display test results
    PROCEDURE display_result(p_test_name VARCHAR2, p_status VARCHAR2, p_message VARCHAR2) IS
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test: ' || p_test_name || ' - Status: ' || p_status || ' - Message: ' || p_message);
    END;
    
BEGIN
    -- -------------------------------------------------------------------
    -- Setup: Update Reserved Stock for Specific Parts to Facilitate Testing
    -- -------------------------------------------------------------------
    BEGIN
        -- Set Reserved stock for 'PN52384R50' to 5
        UPDATE ExternalPart
        SET Reserved = 5
        WHERE PartID = 'PN52384R50';
        
        -- Optionally, set Reserved stock for another part if needed
        -- UPDATE ExternalPart
        -- SET Reserved = 3
        -- WHERE PartID = 'PN12344A21';
        
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Setup: Reserved stock updated successfully.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Setup Error: ' || SQLERRM);
            RAISE;
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 1: Successful Stock Deduction (Stock > Reserved + Quantity)
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN12344A21', 5); -- Stock=10, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Successful Stock Deduction', 'PASS', v_result);
        ELSE
            display_result('Successful Stock Deduction', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Successful Stock Deduction', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 2: Deduction Leading Stock to Exactly Reserved
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R50', 5); -- Stock=10, Reserved=5
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Deduction to Exactly Reserved', 'PASS', v_result);
        ELSE
            display_result('Deduction to Exactly Reserved', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction to Exactly Reserved', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 3: Deduction Leading Stock Below Reserved
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R50', 6); -- Stock=5 (after previous deduction), Reserved=5
        IF v_result = 'Operation cannot be performed. Remaining stock is below the reserved quantity.' THEN
            display_result('Deduction Below Reserved', 'PASS', v_result);
        ELSE
            display_result('Deduction Below Reserved', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Below Reserved', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 4: Deduction with Zero Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', 0); -- Stock=10, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Deduction with Zero Quantity', 'PASS', v_result);
        ELSE
            display_result('Deduction with Zero Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Zero Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 5: Deduction with Negative Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', -5); -- Attempt to increase stock
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify if stock increased by 5
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN18544C21';
                IF v_new_stock = 15 THEN
                    display_result('Deduction with Negative Quantity', 'PASS', 'Stock increased correctly.');
                ELSE
                    display_result('Deduction with Negative Quantity', 'FAIL', 'Stock did not increase as expected.');
                END IF;
            END;
        ELSE
            display_result('Deduction with Negative Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Negative Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 6: Deduction for Non-Existent PartID
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('NON_EXISTENT_PART', 5);
        IF v_result = 'PartID not found.' THEN
            display_result('Deduction for Non-Existent PartID', 'PASS', v_result);
        ELSE
            display_result('Deduction for Non-Existent PartID', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction for Non-Existent PartID', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 7: Deduction Bringing Stock Just Above Reserved
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN12344A21', 4); -- Stock=5, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Deduction Just Above Reserved', 'PASS', v_result);
        ELSE
            display_result('Deduction Just Above Reserved', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Just Above Reserved', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 8: Deduction Bringing Stock Below Reserved by One Unit
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R50', 1); -- Stock=5, Reserved=5
        IF v_result = 'Operation cannot be performed. Remaining stock is below the reserved quantity.' THEN
            display_result('Deduction Below Reserved by One Unit', 'PASS', v_result);
        ELSE
            display_result('Deduction Below Reserved by One Unit', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Below Reserved by One Unit', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 9: Deduction Bringing Stock to Zero When Reserved is Zero
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R10', 10); -- Stock=10, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify stock is zero
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN52384R10';
                IF v_new_stock = 0 THEN
                    display_result('Deduction to Zero Stock', 'PASS', 'Stock is zero as expected.');
                ELSE
                    display_result('Deduction to Zero Stock', 'FAIL', 'Stock is not zero as expected.');
                END IF;
            END;
        ELSE
            display_result('Deduction to Zero Stock', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction to Zero Stock', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 10: Deduction with Extremely Large Quantity Leading to Negative Stock
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', 1000); -- Stock=15, Reserved=0
        IF v_result = 'Operation cannot be performed. Remaining stock is below the reserved quantity.' THEN
            display_result('Deduction with Extremely Large Quantity', 'PASS', v_result);
        ELSE
            display_result('Deduction with Extremely Large Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Extremely Large Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 11: Deduction Bringing Stock Just Above Reserved
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', 10); -- Stock=15, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify stock is 5
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN18544C21';
                IF v_new_stock = 5 THEN
                    display_result('Deduction Bringing Stock to 5', 'PASS', 'Stock deducted correctly to 5.');
                ELSE
                    display_result('Deduction Bringing Stock to 5', 'FAIL', 'Stock is ' || v_new_stock || ' instead of 5.');
                END IF;
            END;
        ELSE
            display_result('Deduction Bringing Stock to 5', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Bringing Stock to 5', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 12: Deduction with Null PartID
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock(NULL, 5);
        IF v_result LIKE 'An error occurred:%' THEN
            display_result('Deduction with Null PartID', 'PASS', v_result);
        ELSE
            display_result('Deduction with Null PartID', 'FAIL', 'Unexpected result: ' || v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Null PartID', 'PASS', 'Handled exception: ' || SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 13: Deduction with Null Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN12344A21', NULL);
        IF v_result LIKE 'An error occurred:%' THEN
            display_result('Deduction with Null Quantity', 'PASS', v_result);
        ELSE
            display_result('Deduction with Null Quantity', 'FAIL', 'Unexpected result: ' || v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Null Quantity', 'PASS', 'Handled exception: ' || SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 14: Deduction for PartID Not in ExternalPart but Exists in Part
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('AS12945S22', 5); -- 'AS12945S22' is internal part, not in ExternalPart
        IF v_result = 'PartID not found.' THEN
            display_result('Deduction for Internal PartID', 'PASS', v_result);
        ELSE
            display_result('Deduction for Internal PartID', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction for Internal PartID', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 15: Deduction with Non-Integer Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN12344A21', 2.5); -- Quantity=2.5
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify if stock decreased by 2.5
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN12344A21';
                IF v_new_stock = 8.5 THEN
                    display_result('Deduction with Non-Integer Quantity', 'PASS', 'Stock deducted correctly by 2.5.');
                ELSE
                    display_result('Deduction with Non-Integer Quantity', 'FAIL', 'Stock is ' || v_new_stock || ' instead of 8.5.');
                END IF;
            END;
        ELSE
            display_result('Deduction with Non-Integer Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Non-Integer Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Final Cleanup: Restore Reserved Stock and Reset Modified Stocks
    -- -------------------------------------------------------------------
    BEGIN
        -- Restore Reserved stock to original values
        UPDATE ExternalPart
        SET Reserved = 0
        WHERE PartID = 'PN52384R50';
        
        -- Reset stock for parts modified during tests
        UPDATE ExternalPart
        SET Stock = 10
        WHERE PartID IN ('PN12344A21', 'PN52384R50', 'PN52384R10', 'PN18544C21');
        
        COMMIT;
        display_result('Final Cleanup', 'PASS', 'Reserved stock and stock levels restored to original values.');
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Final Cleanup', 'FAIL', SQLERRM);
    END;
    
EXCEPTION
    WHEN e_test_failure THEN
        DBMS_OUTPUT.PUT_LINE('One or more tests failed.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected error during testing: ' || SQLERRM);
END;
/
