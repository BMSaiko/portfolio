
#include <stdio.h>
#include "manager.h"
#include <unistd.h>

void simulate_user_actions() {
    printf("\n--- USAC16: Simulating User Railway Management ---\n\n");
    

    printf(">> Action: Train 101 is arriving...\n");
    int assigned_track = assign_track_to_train(101);
    sleep(2);


    printf("\n>> Action: Train 102 is arriving...\n");
    assign_track_to_train(102);
    sleep(2);


    if (assigned_track != -1) {
        printf("\n>> Action: Giving departure order to train on track %d...\n", assigned_track);
        give_departure_order(assigned_track);
        sleep(2);


        printf("\n>> Action: Train has cleared the station, freeing track %d...\n", assigned_track);
        set_track_free(assigned_track);
        sleep(2);
    }
    

    printf("\n>> Action: Setting track 1 as non-operational...\n");
    set_track_nonoperational(1);
    sleep(2);


    printf("\n>> Action: Train 103 is arriving...\n");
    assign_track_to_train(103);
    sleep(2);
    

    printf("\n>> Action: Train 104 is arriving...\n");
    assign_track_to_train(104);
    
    printf("\n--- USAC16: Simulation Finished ---\n");
}


int main() {
	if (!load_config("config.txt")) return 1;

	//show initial status
    print_manager_status();
    

    simulate_user_actions();


    print_manager_status();

    free_manager_data();
    return 0;
}
